package com.flicker.gpt_api_test.realtime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flicker.gpt_api_test.realtime.dto.OpenAiConversationItemCreateRequest;
import com.flicker.gpt_api_test.realtime.dto.OpenAiRequest;
import com.flicker.gpt_api_test.realtime.dto.OpenAiSessionUpdateRequest;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private WebSocketClient openAiWebSocketClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private WebSocketSession clientSession;

    private List<String> audioDeltas = new ArrayList<>(); // 오디오 델타 문자열을 저장할 리스트

    @Value("${openai.api.key}") // Spring에서 API 키 가져오기
    private String openAiApiKey;

    @PostConstruct
    public void init() {
        try {
            openAiWebSocketClient = new WebSocketClient(new URI("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to OpenAI WebSocket");

                    // 세션 설정: updateSession과 같은 방식으로 OpenAI Realtime API에 설정을 보냄
                    sendSessionUpdate();
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JsonNode jsonResponse = objectMapper.readTree(message);
                        System.out.println("Received from OpenAI: " + jsonResponse);

                        // OpenAI 응답에서 필요한 데이터 추출 (예: 텍스트, 오디오 등)
                        String eventType = jsonResponse.path("type").asText();

                        // audio.delta 메시지 처리
                        if (eventType.equals("response.audio.delta")) {
                            // delta에서 오디오 데이터를 가져오기
                            String audioDelta = jsonResponse.path("delta").asText();
                            audioDeltas.add(audioDelta); // 오디오 델타를 리스트에 추가
                        }

                        if (eventType.equals("response.output_item.done")) {
                            // 오디오 델타를 병합하고 Base64로 인코딩
                            byte[] combinedAudio = mergeAudioDeltas(audioDeltas);
                            String finalAudioBase64 = Base64.getEncoder().encodeToString(combinedAudio); // 다시 Base64로 인코딩
                            // 클라이언트에게 오디오 응답 전송
                            if (clientSession != null && clientSession.isOpen()) {
                                clientSession.sendMessage(new TextMessage("Audio data: " + finalAudioBase64));
                            }
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
                                    openAiWebSocketClient.send(jsonMessage);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("OpenAI WebSocket closed: " + reason);
                    reconnect();
                }

                public void reconnect() {
                    executorService.submit(() -> {
                        try {
                            openAiWebSocketClient.reconnectBlocking();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    reconnect();
                }
            };
            // API 키를 제대로 설정해서 사용
            openAiWebSocketClient.addHeader("Authorization", "Bearer " + openAiApiKey);
            openAiWebSocketClient.addHeader("OpenAI-Beta", "realtime=v1");
            openAiWebSocketClient.connect();

        } catch (URISyntaxException e) {
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

    private void sendSessionUpdate() {
        try {
            // 세션 업데이트를 위한 JSON 작성
            String sessionUpdateJson = objectMapper.writeValueAsString(new OpenAiSessionUpdateRequest(
                    """ 
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
                    """ // 세션 설정용 지침
            ));

            // 세션 업데이트 전송
            openAiWebSocketClient.send(sessionUpdateJson);
            System.out.println("Session update sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Client connected: " + session.getId());
        this.clientSession = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String userMessage = message.getPayload();
        System.out.println("Received from client: " + userMessage);

        executorService.submit(() -> {
            try {
                // 대화 항목 생성 요청 전송 / conversation.item.create
                String jsonMessage = objectMapper.writeValueAsString(new OpenAiConversationItemCreateRequest("user", userMessage));
                openAiWebSocketClient.send(jsonMessage);
                // 'response.create'와 'userMessage'를 함께 전달하여 OpenAiRequest 생성 / response.create
//                String responseCreateJsonMessage = objectMapper.writeValueAsString(new OpenAiRequest(userMessage));
                String responseCreateJsonMessage = "{\"type\":\"response.create\"}";
                openAiWebSocketClient.send(responseCreateJsonMessage);
            } catch (Exception e) {
                e.printStackTrace();
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage("Error processing your request."));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("WebSocket error: " + exception.getMessage());
        session.close();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection closed: " + session.getId());
        this.clientSession = null;
    }
}
