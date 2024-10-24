package com.flicker.gpt_api_test.realtime.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flicker.gpt_api_test.realtime.dto.OpenAiSessionRequest;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Component
public class RealtimeApiSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> audioDeltas = new LinkedList<>();
    private final WebSocketSession clientSession;

    public RealtimeApiSocketClient(URI serverUri, WebSocketSession clientSession) {
        super(serverUri);
        this.clientSession = clientSession;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to OpenAI WebSocket");
        // 세션 설정: updateSession과 같은 방식으로 OpenAI Realtime API에 설정을 보냄
        try {
            // 세션 업데이트를 위한 JSON 작성
            String sessionUpdateJson = objectMapper.writeValueAsString(new OpenAiSessionRequest(
                    "너는 대화가 끊기지 않도록 아이가 질문을 하면 대답을 하고 질문을 해야하고, 대답을 하면 그에 맞는 반응을 하고 연관된 질문을 해야해. 한국어로 대화해야 해. 그리고 친근하게 느낄 수 있도록 반말을 사용해" // 세션 설정용 지침
            ));

            // 세션 업데이트 전송
            this.send(sessionUpdateJson);
            System.out.println("Session update sent.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            this.reconnectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        reconnect();
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
