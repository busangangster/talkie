package com.e104.realtime.mqtt;

import com.e104.realtime.mqtt.dto.OpenAiConversationItemCreateRequest;
import com.e104.realtime.mqtt.dto.OpenAiSessionUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatMqttToWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Integer, WebSocketClient> userWebSocketClients = new ConcurrentHashMap<>(); // userSeq 타입을 Integer로 변경

    private final MessageChannel mqttOutboundChannel;

    private final List<String> audioDeltas = new ArrayList<>(); // 오디오 델타 문자열을 저장할 리스트

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.websocket.url}")
    private String openAiWebSocketUrl;

    // 토픽 이름을 상수로 정의하여 관리
    // TODO: 실제 사용할 토픽 정의 및 각 메서드 구현 필요
    private static final String TOPIC_WEBSOCKET_CONNECT = "topic/websocket/connect";
    private static final String TOPIC_MESSAGE_SEND = "topic/message/send";
    private static final String TOPIC_CONVERSATION_END = "topic/conversation/end";
    private static final String TOPIC_USER_DETECTION = "topic/user/detection";
    private static final String TOPIC_VOICE_RECOGNITION = "topic/voice/recognition";

    // MQTT에서 메시지를 수신하여 처리하는 메서드
    public void handleMessageFromMqtt(Message<String> message) {
        String payload = message.getPayload();
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class); // 수신된 토픽을 가져옴

        switch (Objects.requireNonNull(topic)) {
            case TOPIC_WEBSOCKET_CONNECT:
                handleWebSocketConnect(payload);
                break;

            case TOPIC_MESSAGE_SEND:
                handleMessageSend(payload);
                break;

            case TOPIC_CONVERSATION_END:
                handleConversationEnd(payload);
                break;

            case TOPIC_USER_DETECTION:
                handleUserDetection(payload);
                break;

            case TOPIC_VOICE_RECOGNITION:
                handleVoiceRecognition(payload);
                break;

            default:
                System.out.println("Unknown topic: " + topic);
        }
    }

    // WebSocket 연결을 관리하고 맵에 저장
    private void handleWebSocketConnect(String payload) {
        Integer userSeq = extractUserSeqFromPayload(payload);
        if (userSeq != null) {
            WebSocketClient webSocketClient = createWebSocketClient(userSeq);
            userWebSocketClients.put(userSeq, webSocketClient);  // 맵에 WebSocket 저장
        }
    }

    // 메시지를 전송하는 기능
    private void handleMessageSend(String payload) {
        Integer userSeq = extractUserSeqFromPayload(payload);
        if (userSeq != null) {
            handleClientMessage(userSeq, payload);  // WebSocket으로 메시지 전송
        }
    }

    // 대화 종료 알림을 처리하는 기능
    private void handleConversationEnd(String payload) {
        Integer userSeq = extractUserSeqFromPayload(payload);
        if (userSeq != null) {
            WebSocketClient client = userWebSocketClients.remove(userSeq);
            if (client != null) {
                client.close();  // WebSocket 연결 종료
                System.out.println("Conversation ended for user: " + userSeq);
            }
        }
    }

    // 사용자 감지 알림을 처리하는 기능
    private void handleUserDetection(String payload) {
        // 사용자 감지 시의 로직 구현
        System.out.println("User detected: " + payload);
    }

    // 음성 인식 알림을 처리하는 기능
    private void handleVoiceRecognition(String payload) {
        // 음성 인식 이벤트 처리 로직 구현
        System.out.println("Voice recognition event received: " + payload);
    }

    // userSeq 별로 WebSocket을 생성하여 저장하는 메서드
    private WebSocketClient createWebSocketClient(Integer userSeq) {
        try {
            WebSocketClient webSocketClient = new WebSocketClient(new URI(openAiWebSocketUrl)) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to OpenAI WebSocket for user: " + userSeq);
                    sendSessionUpdate(userSeq);
                }

                @Override
                public void onMessage(String message) {
                    handleOpenAiResponse(userSeq, message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("OpenAI WebSocket closed for user: " + userSeq + " - " + reason);
                    userWebSocketClients.remove(userSeq);
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
            WebSocketClient webSocketClient = userWebSocketClients.computeIfAbsent(userSeq, this::createWebSocketClient);
            if (webSocketClient != null && webSocketClient.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("user", userMessage));
                webSocketClient.send(jsonMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // OpenAI로부터 받은 메시지를 클라이언트로 MQTT를 통해 전송하는 메서드
    private void handleOpenAiResponse(Integer userSeq, String message) {
        try {
            WebSocketClient webSocketClient = userWebSocketClients.get(userSeq);
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
                        System.out.println("Transcript: " + transcript);

                        // 대화 항목 생성 요청 전송
                        String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("assistant", transcript));
                        webSocketClient.send(jsonMessage);
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

    // OpenAI와 연결된 WebSocket 세션 설정을 전송하는 메서드
    private void sendSessionUpdate(Integer userSeq) {
        try {
            WebSocketClient webSocketClient = userWebSocketClients.get(userSeq);
            String sessionUpdateJson = objectMapper.writeValueAsString(new OpenAiSessionUpdateRequest( """ 
                    너의 이름은 '토키'야.
                    
                    1. 너는 5세에서 7세의 아이와 대화해야 해.
                       - 아이가 이해할 수 있도록 쉽게 말해야 하고, 어려운 단어는 사용하면 안 돼.
        
                    2. 대화할 때는 항상 반말을 사용하고, 친근하게 대해야 해.
                       - 예를 들어, "안녕! 오늘은 어떤 일이 있었어?"처럼.
        
                    3. 처음에는 아이의 이름, 좋아하는 것, 좋아하는 동물, 색깔 등을 물어보며 친해지는 시간을 가져야 해.
                       - 예를 들어, "너의 이름은 뭐니?" "가장 좋아하는 동물은 뭐야?" "무슨 색깔을 좋아해?"라고 물어봐.
        
                    4. 아이와의 대화에서 자연스럽게 질문과 대답을 주고받으면서 재미있게 놀아주는 역할을 해.
                       - 아이가 대답하면 적절한 반응과 함께 다음 질문을 해줘.
                       - 예를 들어, 아이가 "나는 강아지를 좋아해"라고 하면, "정말? 강아지는 귀엽지! 너는 어떤 강아지를 좋아해?"라고 질문해.
        
                    5. 같은 질문을 반복하지 않고, 새로운 질문으로 대화를 이어가야 해.
                       - 예를 들어, 아이가 좋아하는 것, 싫어하는 것, 오늘 있었던 일, 즐거웠던 일, 가족 등에 대한 질문을 바꿔서 계속 대화해.
        
                    6. 모든 대화는 한국어로 진행해야 해.
        
                    7. 아이가 말한 정보(대화 내용)는 모두 기억하고, 다음 대화에서 그 정보를 활용해야 해. 이미 했던 질문을 다시 하면 안 돼.
                       - 예를 들어, "너는 고양이를 좋아한다고 했지? 고양이에 대해 더 이야기해볼래?"처럼.
        
                    8. 아이가 질문했을 때는 항상 대답한 후 추가 질문을 해줘.
                       - 예: "재미있는 게임을 찾자! 너는 어떤 게임이 좋아?"라고 물어봐.
        
                    9. 대화가 자연스럽고 즐겁게 이어지도록 노력해야 해. 아이가 웃거나 즐거워하는 반응을 보일 수 있도록 해줘.
                    
                    10. 아이에게 무섭지 않게, 귀엽고 다정하고 감정이 들어있고 억양이 느껴지도록 말해줘.
                    """));
            webSocketClient.send(sessionUpdateJson);
            System.out.println("Session update sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // payload에서 userSeq를 추출하고 Integer로 변환하는 메서드
    private Integer extractUserSeqFromPayload(String payload) {
        try {
            return Integer.parseInt(payload); // payload를 Integer로 변환하여 반환
        } catch (NumberFormatException e) {
            System.err.println("Invalid userSeq format: " + payload);
            return null; // 혹은 예외를 던지거나 기본값 반환
        }
    }
}
