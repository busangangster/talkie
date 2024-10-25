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

    @Value("${openai.api.key}") // Spring에서 API 키 가져오기
    private String openAiApiKey;

    @PostConstruct
    public void init() {
        try {
            openAiWebSocketClient = new RealtimeWebSocketClient(new URI("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01"), clientSession);
            // API 키를 제대로 설정해서 사용
            openAiWebSocketClient.addHeader("Authorization", "Bearer " + openAiApiKey);
            openAiWebSocketClient.addHeader("OpenAI-Beta", "realtime=v1");
            openAiWebSocketClient.connect();

        } catch (URISyntaxException e) {
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
