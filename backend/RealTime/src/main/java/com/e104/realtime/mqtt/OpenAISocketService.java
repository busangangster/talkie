package com.e104.realtime.mqtt;

import com.e104.realtime.domain.entity.User;
import com.e104.realtime.mqtt.constant.Instruction;
import com.e104.realtime.mqtt.dto.OpenAiSessionUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class OpenAISocketService {

    private final Map<Integer, WebSocketClient> userWebSocketClients = new ConcurrentHashMap<>(); // userSeq 타입을 Integer로 변경

    private final ObjectMapper objectMapper;

    public void addSocket(int userSeq, WebSocketClient socketClient) {
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
            e.printStackTrace();
        }
    }

    private static String getInstructions(User user) {
        String gender = user.getGender().equals("M") ? "남자" : "여자";
        return Instruction.INSTRUCTION +
        "아이의 이름은: " + user.getName() +
        ", 아이의 나이는: " + user.getAge() +
        ", 아이의 성별은 : " + gender +
        ", 아이가 좋아하는 건: " + user.getFavorite() +
        ". 아이의 인적사항에 알맞게 대화해야해. \n";
    }

    public WebSocketClient getWebSocketClient(int userSeq) {
        return userWebSocketClients.get(userSeq);
    }

    public void removeSocket(int userSeq) {
        WebSocketClient socketClient = userWebSocketClients.get(userSeq);
        socketClient.close();
        userWebSocketClients.remove(userSeq);
    }
}
