package com.e104.realtime.mqtt;

import com.e104.realtime.application.RepoUtil;
import com.e104.realtime.application.Talker;
import com.e104.realtime.application.UserService;
import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.util.TimeChecker;
import com.e104.realtime.domain.User.Question;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.mqtt.constant.Topic;
import com.e104.realtime.mqtt.dto.OpenAiConversationItemCreateRequest;
import com.e104.realtime.mqtt.dto.mqtt.MqttBaseDto;
import com.e104.realtime.redis.hash.Conversation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ChatMqttToWebSocketHandler {

    private final RepoUtil repoUtil;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.websocket.url}")
    private String openAiWebSocketUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageChannel mqttOutboundChannel;
    private final UserService userService;
    private final OpenAISocketService openAISocketService;
    private final AudioDeltaService audioDeltaService;

    public ChatMqttToWebSocketHandler(RepoUtil repoUtil, @Qualifier("mqttOutboundChannel") MessageChannel mqttOutboundChannel, UserService userService, OpenAISocketService openAISocketService, AudioDeltaService audioDeltaService) {
        this.repoUtil = repoUtil;
        this.mqttOutboundChannel = mqttOutboundChannel;
        this.userService = userService;
        this.openAISocketService = openAISocketService;
        this.audioDeltaService = audioDeltaService;
    }

    // MQTT에서 메시지를 수신하여 처리하는 메서드
    public void handleMessageFromMqtt(Message<String> message) {
        String payload = message.getPayload();

        MqttBaseDto dto;
        try {
            dto = objectMapper.readValue(payload, MqttBaseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.info("토픽 입력됨! 데이터: {}", dto);

        if (dto.userSeq() <= 0) {
            log.error("올바르지 않은 사용자 시퀀스입니다. dto: {}", dto);
            return;
        }

        String topic = dto.header();
        if (Objects.isNull(topic)) {
            log.error("토픽이 입력되지 않았습니다. 데이터를 확인해주세요. dto: {}", dto);
            return;
        }

        try {
            switch (topic) {
                case Topic.TOPIC_WEBSOCKET_CONNECT:
                    handleWebSocketConnect(dto);
                    break;

                case Topic.TOPIC_MESSAGE_SEND:
                    handleMessageSend(dto);
                    break;

                case Topic.TOPIC_CONVERSATION_END:
                    handleConversationEnd(dto);
                    break;

                case Topic.TOPIC_USER_DETECTION:
                    handleUserDetection(dto);
                    break;

                case Topic.TOPIC_VOICE_RECOGNITION:
                    handleVoiceRecognition(dto);
                    break;

                default:
                    log.info("Unknown topic: {}", topic);
            }
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 오류가 발생했습니다.", e);
        }
    }

    // WebSocket 연결을 관리하고 맵에 저장
    private void handleWebSocketConnect(MqttBaseDto dto) throws JsonProcessingException {
        int userSeq = dto.userSeq();
        WebSocketClient webSocketClient = createWebSocketClient(userSeq);
        openAISocketService.addSocket(userSeq, webSocketClient);
    }

    // 사용자의 메시지를 chatGPT 에게 전송하는 기능
    private void handleMessageSend(MqttBaseDto dto) throws JsonProcessingException {
        String content = dto.data().get("content");
        Conversation conversation = Conversation.builder()
                .talker(Talker.CHILD.getValue())
                .content(content)
                .build();
        userService.bufferConversation(conversation); // 아이의 대답을 Redis 저장

        handleClientMessage(dto.userSeq(), content);  // WebSocket으로 메시지 전송
    }

    // 대화 종료 알림을 처리하는 기능
    private void handleConversationEnd(MqttBaseDto dto) throws JsonProcessingException {
        userService.saveConversation(dto.userSeq());
    }

    // 사용자 감지 알림을 처리하는 기능
    private void handleUserDetection(MqttBaseDto dto) throws JsonProcessingException {

        // 현재 시각이 밤중이라면 발동하지 않게 하기.
        if (TimeChecker.isNight()) {
            return;
        }

        // 사용자 감지 시의 로직 구현 ( 시간대별로 말을 다르게해야함, 부모의 질문이있으면 그걸 말해줘야함, 아이의 이름을 불러야함 )
        User user = null;
        try {
            user = repoUtil.findUser(dto.userSeq());
        } catch (RestApiException e) {
            log.error("사용자 조회 중 문제가 발생했습니다.", e);
            return;
        }
        List<Question> questions = user.getQuestions();
        Question question = questions.get(questions.size() - 1);
        if (question.isActive()) {
            handleClientMessage(dto.userSeq(), """
                    ''안녕! 난 관리자야. 아이의 부모님이 아래와 같은 질문을 요청했어. 아이에게 인사하고, 질문을 해 줄래?''
                    질문: %s
                    """.formatted(question.getContent()));
            question.updateAnswerd(true); // 질문이 대답되었음을 표시
        } else {
            // 현재 시간 추출
            String clock = TimeChecker.now();
            // 각 시간에 맞는 인사를 해달라고 하기
            handleClientMessage(dto.userSeq(), """
                    ''안녕! 난 관리자야. 지금 아이가 근처에 있어. 지금 시간은 %s이야. 시간에 맞는 인사를 아이에게 해 줄래?''
                    """.formatted(clock));
        }
        log.info("User detected: {}", dto);
    }

    // 대화 시작 신호를 처리하는 기능
    private void handleVoiceRecognition(MqttBaseDto dto) throws JsonProcessingException {
        // 음성 인식 이벤트 처리 로직 구현 ( 응, 왜 불러? 같은 식으로 대답을 해야함 )
        handleClientMessage(dto.userSeq(), """
                ''안녕! 난 관리자야. 지금 아이가 대화를 원하고 있으니, 아이에게 무슨 일이냐고 물어봐줄래?''
                """);
        log.info("Voice recognition event received: {}", dto);
    }

    // userSeq 별로 WebSocket을 생성하여 저장하는 메서드
    private WebSocketClient createWebSocketClient(Integer userSeq) {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(openAiWebSocketUrl)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    log.info("Connected to OpenAI WebSocket for user: {}", userSeq);
                }

                @Override
                public void onMessage(String message) {
                    handleOpenAiResponse(userSeq, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("OpenAI WebSocket closed for user: {} - {}", userSeq, reason);
                    openAISocketService.removeSocket(userSeq);
                }

                @Override
                public void onError(Exception ex) {
                    log.error("소켓 통신 중 오류가 발생했습니다.", ex);
                }
            };

            webSocketClient.addHeader("Authorization", "Bearer " + openAiApiKey);
            webSocketClient.addHeader("OpenAI-Beta", "realtime=v1");
            webSocketClient.connect();
            return webSocketClient;

        } catch (Exception e) {
            log.error("소켓 생성 중 오류가 발생했습니다.", e);
            return null;
        }
    }

    // userSeq에 따라 WebSocket을 통해 사용자 메시지를 전송하는 메서드
    private void handleClientMessage(Integer userSeq, String userMessage) {
        try {
            WebSocketClient webSocketClient = openAISocketService.getWebSocketClient(userSeq);
            if (webSocketClient != null && webSocketClient.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("user", userMessage));
                webSocketClient.send(jsonMessage);
                // 응답 생성 요청 전송
                String responseCreateJsonMessage = "{\"type\":\"response.create\"}";
                webSocketClient.send(responseCreateJsonMessage);
            }
        } catch (Exception e) {
            log.error("사용자의 메시지를 웹소켓으로 전송하는 중 오류가 발생했습니다.", e);
        }
    }

    // OpenAI로부터 받은 메시지를 클라이언트로 MQTT를 통해 전송하는 메서드
    private void handleOpenAiResponse(Integer userSeq, String message) {
        try {
            WebSocketClient webSocketClient = openAISocketService.getWebSocketClient(userSeq);
            JsonNode jsonResponse = objectMapper.readTree(message);
            String eventType = jsonResponse.path("type").asText();

            if ("response.audio.delta".equals(eventType)) {
                // delta에서 오디오 데이터를 가져오기
                String audioDelta = jsonResponse.path("delta").asText();
                audioDeltaService.add(userSeq, audioDelta);
            }

            if ("response.output_item.done".equals(eventType)) {
                // 오디오 델타를 병합하고 Base64로 인코딩
                byte[] combinedAudio = audioDeltaService.squash(userSeq);
                String finalAudioBase64 = Base64.getEncoder().encodeToString(combinedAudio); // 다시 Base64로 인코딩

                // JSON 응답에서 transcript를 추출
                JsonNode contentArray = jsonResponse.path("item").path("content");
                if (contentArray.isArray() && !contentArray.isEmpty()) {
                    // 첫 번째 content에서 type이 audio인 경우에만 transcript 추출
                    if (contentArray.get(0).path("type").asText().equals("audio")) {
                        String transcript = contentArray.get(0).path("transcript").asText(); // 텍스트 응답
                        log.info("Transcript: {}", transcript);

                        Map<String, String> mqttData = Map.of("audio", finalAudioBase64, "transcript", transcript);
                        // 클라이언트에게 오디오 응답 전송
                        mqttOutboundChannel.send(new GenericMessage<>(mqttData.toString()));
                        log.info("데이터 전송 완료!");

                        // 대화 항목 생성 요청 전송
                        String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("assistant", transcript));
                        webSocketClient.send(jsonMessage);

                        // AI 대답 Redis 저장
                        Conversation conversation = Conversation.builder()
                                .talker(Talker.AI.getValue())
                                .content(jsonMessage)
                                .build();
                        userService.bufferConversation(conversation);
                    }
                }
            }
        } catch (Exception e) {
            log.error("음성 메시지를 처리하는 중 문제가 발생했습니다.", e);
        }
    }

}
