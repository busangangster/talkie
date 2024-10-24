package com.flicker.gpt_api_test.realtime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flicker.gpt_api_test.realtime.dto.OpenAiRequest;
import com.flicker.gpt_api_test.realtime.dto.OpenAiSessionRequest;
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

                    // 사용자에게 인사 메시지를 보내는 로직 추가 ( 시작메시지 )
                    sendInitialGreeting();
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
                            JsonNode outputArray = jsonResponse.path("response").path("item").path("content");
                            if (outputArray.isArray() && !outputArray.isEmpty()) {
                                if (outputArray.isArray() && !outputArray.isEmpty()) {
                                    // 첫 번째 content에서 type이 audio인 경우에만 transcript 추출
                                    if (outputArray.get(0).path("type").asText().equals("audio")) {
                                        String transcript = outputArray.get(0).path("transcript").asText(); // 텍스트 응답
                                        System.out.println("Transcript: " + transcript);
                                    }
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
            String sessionUpdateJson = objectMapper.writeValueAsString(new OpenAiSessionRequest(
                    "너는 대화가 끊기지 않도록 아이가 질문을 하면 대답을 하고 질문을 해야하고, 대답을 하면 그에 맞는 반응을 하고 연관된 질문을 해야해. 한국어로 대화해야 해. 그리고 귀여운 목소리를 내야해." // 세션 설정용 지침
            ));

            // 세션 업데이트 전송
            openAiWebSocketClient.send(sessionUpdateJson);
            System.out.println("Session update sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendInitialGreeting() {
        try {
            // 사용자에게 인사하는 메시지를 OpenAI에 전송
            String greetingMessage = objectMapper.writeValueAsString(new OpenAiRequest(
                    "아이한테 자연스럽게 인사를 해봐"
            ));
            openAiWebSocketClient.send(greetingMessage);
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
                // 'response.create'와 'userMessage'를 함께 전달하여 OpenAiRequest 생성
                String jsonMessage = objectMapper.writeValueAsString(new OpenAiRequest(userMessage));
                System.out.println(jsonMessage);  // JSON 문자열 출력
                openAiWebSocketClient.send(jsonMessage);
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
