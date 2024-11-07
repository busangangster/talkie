package com.e104.realtime.mqtt;

import com.e104.realtime.domain.User.User;
import com.e104.realtime.mqtt.constant.Instruction;
import com.e104.realtime.mqtt.dto.OpenAiSessionUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class OpenAISocketService implements Closeable {

    private final Map<Integer, WebSocketClient> userWebSocketClients = new ConcurrentHashMap<>(); // userSeq 타입을 Integer로 변경

    private final ObjectMapper objectMapper;

    public void addSocket(int userSeq, WebSocketClient socketClient) {
        if (Objects.isNull(socketClient)) {
            log.warn("입력받은 소켓이 null입니다. 저장 과정을 건너뜁니다.");
            return;
        }
        if (userWebSocketClients.containsKey(userSeq) && Objects.nonNull(userWebSocketClients.get(userSeq))) {
            log.warn("기존 소켓 연결이 존재합니다. 기존 소켓을 종료합니다.");
            removeSocket(userSeq);
        }
        userWebSocketClients.put(userSeq, socketClient);
    }

    public WebSocketClient getWebSocketClient(int userSeq) {
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
}
