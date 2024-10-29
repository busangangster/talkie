package com.e104.realtime.application;

import com.e104.realtime.domain.vo.DayAnalytics;
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
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", "너는 내가 주는 아이의 자료(점수)를 기반으로 부모님께 요약해줘야해. 예를 들어서 아이의 감정 그래프를 보면, \n" +
                        "기쁨과 놀라움에서 \n" +
                        "높은 반응을 보이며, \n" +
                        "두려움과 슬픔에서 \n" +
                        "낮은 반응을 보여요. \n" +
                        "이는 아이가 주로 긍정적인 \n" +
                        "감정을 더 강하게 느끼는\n" +
                        "경향이 있음을 나타내요\n" + "이런식으로 자료를 요약해줘야해."),
                new MultiChatMessage("user", "기쁨 5, 슬픔 2, 놀라움2, 두려움1, 사랑스러움3, 화남1")
        );

        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    public String summarizeEmotions(List<DayAnalytics> filteredAnalytics) {
        // TODO: 구현
        return null;
    }

    public String summarizeVocabulary(List<DayAnalytics> filteredAnalytics) {
        // TODO: 구현
        return null;
    }

    public String summarizeWordCloud(List<DayAnalytics> filteredAnalytics) {
        // TODO: 구현
        return null;
    }
}
