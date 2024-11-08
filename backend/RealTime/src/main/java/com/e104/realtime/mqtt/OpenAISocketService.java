package com.e104.realtime.mqtt;

import com.e104.realtime.application.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OpenAISocketService implements Closeable {

    private final Map<Integer, RealtimeApiSocket> userWebSocketClients = new ConcurrentHashMap<>(); // userSeq 타입을 Integer로 변경

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.websocket.url}")
    private String openAiWebSocketUrl;

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final MessageChannel mqttOutboundChannel;

    public OpenAISocketService(ObjectMapper objectMapper, UserService userService, @Qualifier("mqttOutboundChannel") MessageChannel mqttOutboundChannel) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    public void addSocket(int userSeq, RealtimeApiSocket socketClient) {
        if (Objects.isNull(socketClient)) {
            log.warn("입력받은 소켓이 null입니다. 저장 과정을 건너뜁니다.");
            return;
        }
        if (userWebSocketClients.containsKey(userSeq) && Objects.nonNull(userWebSocketClients.get(userSeq))) {
            log.warn("기존 소켓 연결이 존재합니다. 소켓 저장을 건너뜁니다.");
            return;
        }
        userWebSocketClients.put(userSeq, socketClient);
    }

    public RealtimeApiSocket getWebSocketClient(int userSeq) {
        if (!userWebSocketClients.containsKey(userSeq)) return null;
        return userWebSocketClients.get(userSeq);
    }

    public void removeSocket(int userSeq) {
        WebSocketClient socketClient = userWebSocketClients.get(userSeq);
        if (Objects.nonNull(socketClient)) {
            socketClient.close();
        }
        userWebSocketClients.remove(userSeq);
    }

    @Override
    public void close() {
        userWebSocketClients.forEach((key, value) -> value.close());
    }

    public RealtimeApiSocket createSocket(int userSeq) {
        try {
            RealtimeApiSocket webSocketClient = RealtimeApiSocket.builder()
                    .openAiWebSocketUrl(openAiWebSocketUrl)
                    .userSeq(userSeq)
                    .objectMapper(objectMapper)
                    .closeHandler(this::removeSocket)
                    .mqttOutboundChannel(mqttOutboundChannel)
                    .userService(userService)
                    .build();
            webSocketClient.addHeader("Authorization", "Bearer " + openAiApiKey);
            webSocketClient.addHeader("OpenAI-Beta", "realtime=v1");
            webSocketClient.connectBlocking();
            userWebSocketClients.put(userSeq, webSocketClient);
            return webSocketClient;
        } catch (Exception e) {
            log.error("소켓 생성 중 오류가 발생했습니다.", e);
        }
        return null;
    }

    public boolean isConnected(int userSeq) {
        if (!userWebSocketClients.containsKey(userSeq)) return false;
        WebSocketClient webSocketClient = userWebSocketClients.get(userSeq);
        return Objects.nonNull(webSocketClient) && webSocketClient.isOpen();
    }
}
