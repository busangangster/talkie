package com.e104.realtime.application;

import com.e104.realtime.domain.vo.*;
import com.e104.realtime.dto.AnswerCreateRequest;
import com.e104.realtime.dto.QuestionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuilderUtil {

    public Question buildQuestion(String content) {
        return Question.builder()
                .content(content)
                .build();
    }

    public Answer buildAnswer(String content) {
        return Answer.builder()
                .content(content)
                .build();
    }

    public DayAnalytics buildDayAnalytics(double vocabularyScore, int happyScore, int sadScore, int angryScore, int amazingScore, int scaryScore, int conversationCount) {
        return DayAnalytics.builder()
                .vocabularyScore(vocabularyScore)
                .happyScore(happyScore)
                .sadScore(sadScore)
                .angryScore(angryScore)
                .amazingScore(amazingScore)
                .scaryScore(scaryScore)
                .conversationCount(conversationCount)
                .build();
    }

    public DayWordCloud buildDayWordCloud(String word, int count) {
        return DayWordCloud.builder()
                .word(word)
                .count(count)
                .build();
    }

    public WeekWordCloud buildWeekWordCloud(String word, int count) {
        return WeekWordCloud.builder()
                .word(word)
                .count(count)
                .build();
    }

    public WeekAnalytics buildWeekAnalytics(String emotionSummary, String vocabularySummary, String wordCloudSummary, int year, int month, int week) {
        return WeekAnalytics.builder()
                .emotionSummary(emotionSummary)
                .vocabularySummary(vocabularySummary)
                .wordCloudSummary(wordCloudSummary)
                .year(year)
                .month(month)
                .week(week)
                .build();
    }
}
