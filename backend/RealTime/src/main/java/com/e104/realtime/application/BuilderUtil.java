package com.e104.realtime.application;

import com.e104.realtime.domain.ConversationAnalytics.ConversationAnalytics;
import com.e104.realtime.domain.ConversationAnalytics.ConversationSummary;
import com.e104.realtime.domain.DayAnalytics.DayAnalytics;
import com.e104.realtime.domain.DayAnalytics.DayWordCloud;
import com.e104.realtime.domain.User.Answer;
import com.e104.realtime.domain.User.Question;
import com.e104.realtime.domain.WeekAnalytics.WeekAnalytics;
import com.e104.realtime.domain.WeekAnalytics.WeekWordCloud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public DayAnalytics buildDayAnalytics(double vocabularyScore, int happyScore, int loveScore, int sadScore, int angryScore, int amazingScore, int scaryScore, int conversationCount) {
        return DayAnalytics.builder()
                .vocabularyScore(vocabularyScore)
                .happyScore(happyScore)
                .loveScore(loveScore)
                .sadScore(sadScore)
                .angryScore(angryScore)
                .amazingScore(amazingScore)
                .scaryScore(scaryScore)
                .conversationCount(conversationCount)
                .createdAt(LocalDate.now())
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

    public WeekAnalytics buildWeekAnalytics(String emotionSummary, String vocabularySummary, String wordCloudSummary, String countSummary, int year, int month, int week) {
        return WeekAnalytics.builder()
                .emotionSummary(emotionSummary)
                .vocabularySummary(vocabularySummary)
                .wordCloudSummary(wordCloudSummary)
                .countSummary(countSummary)
                .year(year)
                .month(month)
                .week(week)
                .build();
    }


    public ConversationSummary buildConversationSummary(String content) {
        return ConversationSummary.builder()
                .content(content)
                .build();
    }

    public ConversationAnalytics buildConversationAnalytics(String title, String emotionSummary, String vocabularySummary, String wordCloudSummary) {
        return ConversationAnalytics.builder()
                .title(title)
                .emotionSummary(emotionSummary)
                .vocabularySummary(vocabularySummary)
                .wordCloudSummary(wordCloudSummary)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
