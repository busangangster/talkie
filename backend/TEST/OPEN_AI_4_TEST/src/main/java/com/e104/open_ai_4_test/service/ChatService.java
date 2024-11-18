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
                                주어진 대화 목록을 바탕으로 아이가 어떤 관심사를 가지고 있는지 분석하고, 정확한 양식을 제시해줘.
                                각 관심사는 세부 주제별로 분류하고, 언급된 횟수를 포함하며, 관심도가 높은 순으로 정렬해 줘.
                                모든 항목은 독립적으로 나열해야 하며, 같은 주제라도 세부 항목으로 구분해서 각각 횟수를 표시해야 해.
                                전체적인 대화 흐름을 파악하고, 아이의 대화에서만 관심사를 뽑아내야 해.
                            
                                예시:
                                대화 목록:
                                아이: "토키, 오늘 날씨가 어때?"
                                토키: "오늘은 맑고 따뜻한 날씨야!"
                                아이: "그럼 산책하러 나가볼까?"
                                토키: "좋아, 산책하면서 재밌게 놀자!"
                            
                                출력 형식:
                                1. 날씨 / 2번
                                2. 산책 / 2번
                                3. 사자 / 1번
                                4. 코끼리 / 1번
                                5. 사랑 / 1번
                            
                                정확히 이 양식과 같이 결과를 작성해 줘.
                            """),
                    new MultiChatMessage("user", message)
            );
            // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
            return chatgptService.multiChat(messages);
    }

    private String getString() {
        // weekWordClouds 데이터를 2차원 배열로 선언
        String[][] weekWordCloudsArray = {
                {"아이", "토키야, 요즘 코끼리가 정말 재밌어 보여!"},
                {"토키", "코끼리는 정말 신기한 동물이야. 너도 좋아하구나!"},
                {"아이", "날씨가 추워져서 놀이터 가기 싫어."},
                {"토키", "그렇지? 날씨가 추우면 집에서 놀아도 좋지 않을까?"},
                {"아이", "유치원에서 친구들이랑 노는 게 제일 재밌어!"},
                {"토키", "맞아, 친구들과 함께 있으면 정말 즐거운 시간이 될 거야!"},
                {"아이", "동물원에서 많은 동물들을 보고 싶어."},
                {"토키", "동물원에 가면 재미있는 동물들이 많을 거야. 꼭 가보자!"},
                {"아이", "엄마랑 같이 가면 더 즐거울 것 같아!"},
                {"토키", "엄마랑 함께하면 더 특별한 시간이 될 거야. 꼭 같이 가자!"}
        };

        StringBuilder message = new StringBuilder();
        for (String[] strings : weekWordCloudsArray) {
            message.append(strings[0]).append(": ")
                    .append(strings[1]).append(" \n");
        }
        return message.toString();
    }

}
