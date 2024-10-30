package com.e104.realtime.application;

import com.e104.realtime.domain.entity.DayAnalytics;
import com.e104.realtime.domain.entity.WeekWordCloud;
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

    public String summarizeEmotions(List<DayAnalytics> dayAnalyticsList) {
        String message = getWeekEmotionMessage(dayAnalyticsList);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                    너는 내가 제공하는 아이의 일주일치 감정 점수를 바탕으로 부모님께 요약해줘야 해요. 요약할 때는 문장을 '~요'로 끝내고, 아이의 감정 패턴을 이해하기 쉽게 설명해 주세요. 예를 들어:
                    
                    '아이가 일주일 동안 보인 감정을 분석해 보면, 기쁨과 놀라움에서 높은 반응을 보이며, 두려움과 슬픔에서 낮은 반응을 보여요. 이는 아이가 주로 긍정적인 감정을 더 강하게 느끼는 경향이 있음을 나타내요.'
                    
                    이와 비슷하게 감정 점수를 요약해주고, 부모님께 아이의 감정 상태에 맞춘 간단한 조언도 제안해 주세요. 예를 들어:
                    
                    '아이의 긍정적인 감정을 키워주기 위해 매일 즐거웠던 일에 대해 이야기해 보세요.' 또는 '아이에게 새로운 활동을 시도하게 하여 놀라움을 느끼게 해주는 것도 좋은 방법이에요.'
                    """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private static String getWeekEmotionMessage(List<DayAnalytics> dayAnalyticsList) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i <= dayAnalyticsList.size(); i++) {
            DayAnalytics dayAnalytics = dayAnalyticsList.get(i - 1);
            message.append("Day ").append(i).append(":\n").append("기쁨: ").append(dayAnalytics.getHappyScore()).append("\n").append("사랑스러움: ").append(dayAnalytics.getLoveScore()).append("\n").append("슬픔: ").append(dayAnalytics.getSadScore()).append("\n").append("화남: ").append(dayAnalytics.getAngryScore()).append("\n").append("놀라움: ").append(dayAnalytics.getAmazingScore()).append("\n").append("두려움: ").append(dayAnalytics.getScaryScore()).append("\n");
        }
        return message.toString();
    }

    public String summarizeVocabulary(List<DayAnalytics> dayAnalyticsList) {
        String message = getWeekVocabularyMessage(dayAnalyticsList);
        List<MultiChatMessage> messages = Arrays.asList(
                new MultiChatMessage("system", """
                    너는 내가 제공하는 아이의 일주일치 어휘력 점수를 바탕으로 부모님께 요약해줘야 해요. 요약할 때는 문장을 '~요'로 끝내고, 아이의 어휘력 변화를 쉽게 이해할 수 있게 설명해 주세요. 예를 들어:
                    
                    '아이가 일주일 동안 보인 어휘력 점수를 보면, 어휘력이 점점 증가하는 게 보이네요. 이번 주 어휘력 평균 점수는 4.0점으로, 동나이 어린이의 평균 점수인 3.75보다 높아요. 아이에게 칭찬해 주세요!'
                    
                    이와 비슷한 형식으로 어휘력 점수를 요약해 주고, 부모님께 아이를 격려할 방법도 간단히 제안해 주세요. 예를 들어:
                    
                    '아이가 관심을 가진 주제에 대해 질문을 해보거나, 새로운 단어를 배울 때마다 스티커를 주며 칭찬해 주세요!' 또는 '아이와 함께 동화책을 읽고 새로운 단어를 찾아보는 활동을 하시면 더욱 좋을 것 같아요!'
                    """
                ),
                new MultiChatMessage("user", message)
        );
        // multiChat 메서드를 통해 OpenAI API에 연속 메시지를 전달하고 응답을 받습니다.
        return chatgptService.multiChat(messages);
    }

    private String getWeekVocabularyMessage(List<DayAnalytics> dayAnalyticsList) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i <= dayAnalyticsList.size(); i++) {
            DayAnalytics dayAnalytics = dayAnalyticsList.get(i - 1);
            message.append("Day ").append(i).append(":\n").append("어휘 점수: ").append(dayAnalytics.getVocabularyScore()).append("\n");
        }
        message.append("이번 주 어휘력 평균 점수: ").append(dayAnalyticsList.stream().mapToDouble(DayAnalytics::getVocabularyScore).average().orElse(0)).append("\n");
        message.append(("동나이 어린이의 어휘력 평균 점수: 3.75\n"));
        return message.toString();
    }

    public String summarizeWeekWordCloud(List<WeekWordCloud> weekWordClouds) {
        String message = getWeekWordCloudMessage(weekWordClouds);
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

    private String getWeekWordCloudMessage(List<WeekWordCloud> weekWordClouds) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i <= weekWordClouds.size(); i++) {
            WeekWordCloud weekWordCloud = weekWordClouds.get(i - 1);
            message.append("Word ").append(i).append(":\n").append(weekWordCloud.getWord()).append(": ").append(weekWordCloud.getCount()).append("개\n");
        }
        return message.toString();
    }
}
