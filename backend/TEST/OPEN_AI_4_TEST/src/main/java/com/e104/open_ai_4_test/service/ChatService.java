package com.e104.open_ai_4_test.service;

import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatgptService chatgptService;

    public String testMultiChat() {
        String message = getString();
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                    너는 내가 제공하는 아이의 관심사 목록(워드클라우드)을 바탕으로 부모님께 요약해줘야 해요. 아이가 관심을 가진 주제를 분류해서 설명해 주세요. 예를 들어:
            
                    '아이는 이번주의 주로 장난감, 가족, 계절, 음식 등이 아이의 관심을 보였어요.'
            
                    이렇게 워드클라우드를 분석하고 요약해주고, 부모님께 아이와 어떤 활동을 하면 좋을지 제안도 해줘요. 예를 들어, '아이가 좋아하는 장난감을 함께 가지고 놀거나 놀이공원을 방문해보시는 건 어때요?'와 같이 구체적인 제안도 추가해 주세요.
                    """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getString() {
        // weekWordClouds 데이터를 2차원 배열로 선언
        String[][] weekWordCloudsArray = {
                {"코끼리", "10"},
                {"놀이공원", "8"},
                {"추워", "12"},
                {"유치원", "5"},
                {"동물원", "7"},
                {"비행기", "6"},
                {"엄마", "4"}
        };
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < weekWordCloudsArray.length; i++) {
            message.append("Word ").append(i + 1).append(":\n")
                    .append(weekWordCloudsArray[i][0]).append(": ")
                    .append(weekWordCloudsArray[i][1]).append("개 \n");
        }
        return message.toString();
    }

}
