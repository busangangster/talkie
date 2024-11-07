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

    // OpenAI와 연결된 WebSocket 세션 설정을 전송하는 메서드
    public void sendSessionUpdate(User user) {
        try {
            WebSocketClient webSocketClient = userWebSocketClients.get(user.getUserSeq());
            String sessionUpdateJson = objectMapper.writeValueAsString(new OpenAiSessionUpdateRequest(getInstructions(user)));
            webSocketClient.send(sessionUpdateJson);
            log.info("Session update sent.");
        } catch (Exception e) {
            log.error("업데이트된 세션을 전송하는 중 문제가 발생했습니다.", e);
        }
    }

    private static String getInstructions(User user) {
        String gender = user.getGender().equals("M") ? "남자" : "여자";
        return Instruction.INSTRUCTION +
                "아이의 이름은: " + user.getName() +
                ", 아이의 나이는: " + user.getAge() +
                ", 아이의 성별은 : " + gender +
                ", 아이가 좋아하는 건: " + user.getFavorite() +
                ",아이의 특이사항은: " + user.getRemark() +
                ". 아이의 인적사항에 알맞게 대화해야해. \n";
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
