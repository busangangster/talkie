package com.e104.realtime.mqtt;

import com.e104.realtime.application.Talker;
import com.e104.realtime.application.UserService;
import com.e104.realtime.mqtt.constant.Instruction;
import com.e104.realtime.mqtt.dto.OpenAiConversationItemCreateRequest;
import com.e104.realtime.mqtt.dto.OpenAiSessionUpdateRequest;
import com.e104.realtime.redis.hash.Conversation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public class RealtimeApiSocket extends WebSocketClient {

    private final AudioDelta audioDelta = new AudioDelta();

    private final int userSeq;
    private final ObjectMapper objectMapper;
    private final Consumer<Integer> closeHandler;
    private final MessageChannel mqttOutboundChannel;
    private final UserService userService;

    @Getter
    private boolean initialized = false;

    @Builder
    public RealtimeApiSocket(String openAiWebSocketUrl, int userSeq, ObjectMapper objectMapper, Consumer<Integer> closeHandler, MessageChannel mqttOutboundChannel, UserService userService) throws URISyntaxException {
        super(new URI(openAiWebSocketUrl));
        this.userSeq = userSeq;
        this.objectMapper = objectMapper;
        this.closeHandler = closeHandler;
        this.mqttOutboundChannel = mqttOutboundChannel;
        this.userService = userService;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connected to OpenAI WebSocket for user: {}", userSeq);
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonNode jsonResponse = objectMapper.readTree(message);
            String eventType = jsonResponse.path("type").asText();

            log.info("OpenAI로부터 받은 이벤트 타입: {}", eventType);

            if ("session.created".equals(eventType)) {
                log.info("세션 생성 확인. 세션 업데이트 요청을 전송합니다.");
                this.send(objectMapper.writeValueAsString(new OpenAiSessionUpdateRequest(Instruction.INSTRUCTION)));
            }

            if ("session.updated".equals(eventType)) {
                log.info("세션 업데이트가 완료되었습니다.");
                initialized = true;
            }

            if ("response.audio.delta".equals(eventType)) {
                // delta에서 오디오 데이터를 가져오기
                String audioDeltaPiece = JsonParser.getDelta(jsonResponse);
                audioDelta.add(audioDeltaPiece);
            }

            if ("response.output_item.done".equals(eventType)) {
                // 오디오 델타를 병합하고 Base64로 인코딩
                byte[] combinedAudio = audioDelta.squash();
                String finalAudioBase64 = Base64.getEncoder().encodeToString(combinedAudio); // 다시 Base64로 인코딩

                // JSON 응답에서 transcript를 추출
                String transcript = JsonParser.extractTranscriptFromResponseItemDone(jsonResponse);
                log.info("Transcript: {}", transcript);

                Map<String, String> mqttData = Map.of("audio", finalAudioBase64, "transcript", Objects.requireNonNull(transcript));
                // 클라이언트에게 오디오 응답 전송
                mqttOutboundChannel.send(new GenericMessage<>(objectMapper.writeValueAsString(mqttData)));
                log.info("데이터 전송 완료!");

                // 대화 항목 생성 요청 전송
                String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("assistant", transcript));
                this.send(jsonMessage);

                // AI 대답 Redis 저장
                Conversation conversation = Conversation.builder()
                        .talker(Talker.AI.getValue())
                        .content(jsonMessage)
                        .build();
                userService.bufferConversation(conversation);
            }
        } catch (Exception e) {
            log.error("음성 메시지를 처리하는 중 문제가 발생했습니다.", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info("소켓을 닫습니다. userSeq: {}", userSeq);
        closeHandler.accept(userSeq);
    }

    @Override
    public void onError(Exception e) {
        log.error("소켓 통신 중 오류가 발생했습니다.", e);
    }

    private static final class JsonParser {
        private static String extractTranscriptFromResponseItemDone(JsonNode jsonResponse) {
            JsonNode contentArray = jsonResponse.path("item").path("content");
            return contentArray.get(0).path("transcript").asText();
        }

        private static String getDelta(JsonNode jsonResponse) {
            return jsonResponse.path("delta").asText();
        }

    }

}
