package com.e104.realtime.mqtt;

import com.e104.realtime.application.RepoUtil;
import com.e104.realtime.application.Talker;
import com.e104.realtime.application.UserService;
import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.util.TimeChecker;
import com.e104.realtime.domain.User.Question;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.mqtt.constant.Topic;
import com.e104.realtime.mqtt.dto.*;
import com.e104.realtime.redis.hash.Conversation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMqttToWebSocketHandler {

    public static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";
    private final RepoUtil repoUtil;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.websocket.url}")
    private String openAiWebSocketUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> audioDeltas = Collections.synchronizedList(new ArrayList<>()); // 오디오 델타 문자열을 저장할 리스트

    private final MessageChannel mqttOutboundChannel;
    private final UserService userService;
    private final OpenAISocketService openAISocketService;

    // MQTT에서 메시지를 수신하여 처리하는 메서드
    public void handleMessageFromMqtt(Message<String> message) {
        String payload = message.getPayload();
        Optional<String> topic = Optional.ofNullable(message.getHeaders().get(MQTT_RECEIVED_TOPIC, String.class)); // 수신된 토픽을 가져옴
        if(topic.isEmpty()) throw new NullPointerException("토픽이 입력되지 않았습니다.");
        try {
            switch (topic.get()) {
                case Topic.TOPIC_WEBSOCKET_CONNECT:
                    handleWebSocketConnect(payload);
                    break;

                case Topic.TOPIC_MESSAGE_SEND:
                    handleMessageSend(payload);
                    break;

                case Topic.TOPIC_CONVERSATION_END:
                    handleConversationEnd(payload);
                    break;

                case Topic.TOPIC_USER_DETECTION:
                    handleUserDetection(payload);
                    break;

                case Topic.TOPIC_VOICE_RECOGNITION:
                    handleVoiceRecognition(payload);
                    break;

                default:
                    log.info("Unknown topic: " + topic);
            }
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 오류가 발생했습니다.", e);
        }
    }

    // WebSocket 연결을 관리하고 맵에 저장
    private void handleWebSocketConnect(String payload) throws JsonProcessingException {
        MqttWebsocketConnectDto dto = objectMapper.readValue(payload, MqttWebsocketConnectDto.class);
        int userSeq = dto.userSeq();
        if(userSeq == 0) throw new NullPointerException("잘못된 사용자 시퀀스입니다.");
        WebSocketClient webSocketClient = createWebSocketClient(userSeq);
        openAISocketService.addSocket(userSeq, webSocketClient);
    }

    // 사용자의 메시지를 chatGPT 에게 전송하는 기능
    private void handleMessageSend(String payload) throws JsonProcessingException {
        MqttMessageSendDto dto = objectMapper.readValue(payload, MqttMessageSendDto.class);
        int userSeq = dto.userSeq();
        if(userSeq == 0) throw new NullPointerException("잘못된 사용자 시퀀스입니다.");

        Conversation conversation = Conversation.builder()
                .talker(Talker.CHILD.getValue())
                .content(dto.content())
                .build();
        userService.bufferConversation(conversation); // 아이의 대답을 Redis 저장

        handleClientMessage(userSeq, dto.content());  // WebSocket으로 메시지 전송
    }

    // 대화 종료 알림을 처리하는 기능
    private void handleConversationEnd(String payload) throws JsonProcessingException {
        MqttConversationEndDto dto = objectMapper.readValue(payload, MqttConversationEndDto.class);
        int userSeq = dto.userSeq();
        if(userSeq == 0) throw new NullPointerException("잘못된 사용자 시퀀스입니다.");
        userService.saveConversation(userSeq);
    }

    // 사용자 감지 알림을 처리하는 기능
    private void handleUserDetection(String payload) throws JsonProcessingException {

        // 현재 시각이 밤중이라면 발동하지 않게 하기.
        if(TimeChecker.isNight()) {
            return;
        }

        // 사용자 감지 시의 로직 구현 ( 시간대별로 말을 다르게해야함, 부모의 질문이있으면 그걸 말해줘야함, 아이의 이름을 불러야함 )
        MqttUserDetectionDto dto = objectMapper.readValue(payload, MqttUserDetectionDto.class);
        User user = repoUtil.findUser(dto.getUserSeq());
        List<Question> questions = user.getQuestions();
        Question question = questions.get(questions.size() - 1);
        if (question.isActive()) {
            handleClientMessage(dto.getUserSeq(), """
                    ''안녕! 난 관리자야. 아이의 부모님이 아래와 같은 질문을 요청했어. 아이에게 인사하고, 질문을 해 줄래?''
                    질문: %s
                    """.formatted(question.getContent()));
            question.updateAnswerd(); // 질문이 대답되었음을 표시
        }
        else {
            // 현재 시간 추출
            String clock = TimeChecker.now();
            // 각 시간에 맞는 인사를 해달라고 하기
            handleClientMessage(dto.getUserSeq(), """
                    ''안녕! 난 관리자야. 지금 아이가 근처에 있어. 지금 시간은 %s이야. 시간에 맞는 인사를 아이에게 해 줄래?''
                    """.formatted(clock));
        }
        log.info("User detected: {}", payload);
    }

    // 대화 시작 신호를 처리하는 기능
    private void handleVoiceRecognition(String payload) throws JsonProcessingException {
        // 음성 인식 이벤트 처리 로직 구현 ( 응, 왜 불러? 같은 식으로 대답을 해야함 )
        MqttVoiceRecognitionDto dto = objectMapper.readValue(payload, MqttVoiceRecognitionDto.class);
        int userSeq = dto.userSeq();
        if(userSeq == 0) throw new NullPointerException("잘못된 사용자 시퀀스입니다.");
        handleClientMessage(userSeq, """
                ''안녕! 난 관리자야. 지금 아이가 대화를 원하고 있으니, 아이에게 무슨 일이냐고 물어봐줄래?''
                """);
        log.info("Voice recognition event received: {}", payload);
    }

    // userSeq 별로 WebSocket을 생성하여 저장하는 메서드
    private WebSocketClient createWebSocketClient(Integer userSeq) {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(openAiWebSocketUrl)) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    log.info("Connected to OpenAI WebSocket for user: {}", userSeq);
                }

                @Override
                public void onMessage(String message) {
                    handleOpenAiResponse(userSeq, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.info("OpenAI WebSocket closed for user: {} - {}", userSeq, reason);
//                    userWebSocketClients.remove(userSeq);
                    openAISocketService.removeSocket(userSeq);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            webSocketClient.addHeader("Authorization", "Bearer " + openAiApiKey);
            webSocketClient.connect();
            return webSocketClient;

        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
                audioDeltas.add(audioDelta); // 오디오 델타를 리스트에 추가
            }

            if ("response.output_item.done".equals(eventType)) {
                // 오디오 델타를 병합하고 Base64로 인코딩
                byte[] combinedAudio = mergeAudioDeltas(audioDeltas);
                String finalAudioBase64 = Base64.getEncoder().encodeToString(combinedAudio); // 다시 Base64로 인코딩
                // 클라이언트에게 오디오 응답 전송
                // userSeq별 고유한 토픽으로 메시지 전송
                String userSpecificTopic = "response/audio/" + userSeq;
                mqttOutboundChannel.send(new GenericMessage<>(finalAudioBase64, Map.of("mqtt_topic", userSpecificTopic)));
                // 초기화
                audioDeltas.clear(); // 오디오 델타 리스트 초기화

                // JSON 응답에서 transcript를 추출
                JsonNode contentArray = jsonResponse.path("item").path("content");
                if (contentArray.isArray() && !contentArray.isEmpty()) {
                    // 첫 번째 content에서 type이 audio인 경우에만 transcript 추출
                    if (contentArray.get(0).path("type").asText().equals("audio")) {
                        String transcript = contentArray.get(0).path("transcript").asText(); // 텍스트 응답
                        log.info("Transcript: " + transcript);

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
            e.printStackTrace();
        }
    }

    // 오디오 델타를 디코딩하고 합치는 메서드
    private byte[] mergeAudioDeltas(List<String> audioDeltas) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String delta : audioDeltas) {
            byte[] audioBytes = Base64.getDecoder().decode(delta);
            outputStream.write(audioBytes);
        }

        return outputStream.toByteArray(); // 합쳐진 오디오 데이터를 반환
    }

}
