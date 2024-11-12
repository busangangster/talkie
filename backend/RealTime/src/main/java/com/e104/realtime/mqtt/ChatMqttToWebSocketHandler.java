package com.e104.realtime.mqtt;

import com.e104.realtime.application.RepoUtil;
import com.e104.realtime.application.Talker;
import com.e104.realtime.application.UserService;
import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.util.TimeChecker;
import com.e104.realtime.domain.User.Question;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.mqtt.constant.Instruction;
import com.e104.realtime.mqtt.constant.Topic;
import com.e104.realtime.mqtt.dto.OpenAiConversationItemCreateRequest;
import com.e104.realtime.mqtt.dto.mqtt.MqttBaseDto;
import com.e104.realtime.redis.hash.Conversation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatMqttToWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RepoUtil repoUtil;
    private final UserService userService;
    private final OpenAISocketService openAISocketService;

    // MQTT에서 메시지를 수신하여 처리하는 메서드
    public void handleMessageFromMqtt(Message<String> message) {
        String payload = message.getPayload();

        MqttBaseDto dto;
        try {
            dto = objectMapper.readValue(payload, MqttBaseDto.class);
        } catch (JsonProcessingException e) {
            log.error("JSON 파싱 중 오류가 발생했습니다.", e);
            return;
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

        switch (topic) {
            case Topic.TOPIC_USER_DETECTION:
                handleUserDetection(dto);
                break;
            case Topic.TOPIC_MESSAGE_SEND:
                handleMessageSend(dto);
                break;
            case Topic.TOPIC_CONVERSATION_END:
                handleConversationEnd(dto);
                break;
            default:
                log.info("Unknown topic: {}", topic);
        }
    }

    // 사용자의 메시지를 chatGPT 에게 전송하는 기능
    private void handleMessageSend(MqttBaseDto dto) {

        String content = dto.data().get("content");

        // 대화가 진행 중이면 대화를 보냄
        if (userService.isTalkingNow(dto.userSeq())) {
            sendClientMessageToOpenaiWebsocket(dto.userSeq(), content);  // WebSocket으로 메시지 전송
        }
        // 대화가 진행 중이 아니라면
        else {
            // '토키야'라는 말이 있으면 대화를 시작함.
            if (content.contains("토끼야") || content.contains("토키야")) {
                sendClientMessageToOpenaiWebsocket(dto.userSeq(), Instruction.START_CONVERSATION + content);
            }
            else {
                // '토키야'라는 말이 없으면 그냥 끝냄
                log.info("대화가 시작되지 않음. '토키야'로 대화 시작 필요.");
                return;
            }
        }

        Conversation conversation = Conversation.builder()
                .talker(Talker.CHILD.getValue())
                .userSeq(dto.userSeq())
                .content(content)
                .build();
        userService.bufferConversation(conversation); // 아이의 대답을 Redis 저장

    }

    // 대화 종료 알림을 처리하는 기능
    private void handleConversationEnd(MqttBaseDto dto) {
        log.info("대화 저장 시작");
        userService.saveConversation(dto.userSeq());
        log.info("대화 저장 끝");
//        openAISocketService.removeSocket(dto.userSeq());
    }

    // 사용자 감지 알림을 처리하는 기능
    private void handleUserDetection(MqttBaseDto dto) {

        // 이미 대화중이라면 발동하지 말 것.
//        if (openAISocketService.isConnected(dto.userSeq())) return;
        if (userService.isTalkingNow(dto.userSeq())) return;

        // 현재 시각이 밤중이라면 발동하지 않게 하기.
        if (TimeChecker.isNight()) {
            return;
        }

        // 사용자 감지 시의 로직 구현 ( 시간대별로 말을 다르게해야함, 부모의 질문이있으면 그걸 말해줘야함, 아이의 이름을 불러야함 )
        User user;
        try {
            user = repoUtil.findUser(dto.userSeq());
        } catch (RestApiException e) {
            log.error("사용자 조회 중 문제가 발생했습니다.", e);
            return;
        }

        // TODO: 이거 추상화하기
        List<Question> questions = user.getQuestions();
        Question question = questions.get(questions.size() - 1);

        if (question.isActive()) {
            sendClientMessageToOpenaiWebsocket(dto.userSeq(), Instruction.ASK_QUESTION.formatted(question.getContent()));
            question.updateAnswerd(true); // 질문이 대답되었음을 표시
        } else {
            // 현재 시간 추출
            String clock = TimeChecker.now();
            // 각 시간에 맞는 인사를 해달라고 하기
            sendClientMessageToOpenaiWebsocket(dto.userSeq(), Instruction.GREETING.formatted(clock));
        }
        log.info("User detected: {}", dto);
    }

    // userSeq에 따라 WebSocket을 통해 사용자 메시지를 전송하는 메서드
    private void sendClientMessageToOpenaiWebsocket(Integer userSeq, String userMessage) {
        try {
            RealtimeApiSocket webSocketClient = openAISocketService.getWebSocketClient(userSeq);

            if (Objects.isNull(webSocketClient)) {
                log.info("웹소켓이 존재하지 않습니다! 소켓 연결을 진행합니다.");
                webSocketClient = openAISocketService.createSocket(userSeq);
                // 소켓이 초기화될 때까지 대기
                log.info("소켓 초기화 대기중...");
                while (true) {
                    Thread.sleep(500);
                    if (webSocketClient.isInitialized()) break;
                }
                log.info("초기화 완료.");

            }

            String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("user", userMessage));
            webSocketClient.send(jsonMessage);
            // 응답 생성 요청 전송
            String responseCreateJsonMessage = "{\"type\":\"response.create\"}";
            webSocketClient.send(responseCreateJsonMessage);

        } catch (Exception e) {
            log.error("사용자의 메시지를 웹소켓으로 전송하는 중 오류가 발생했습니다.", e);
        }
    }

}
