package com.e104.realtime.dto;

import com.e104.realtime.domain.DayAnalytics.DayAnalytics;
import com.e104.realtime.domain.WeekAnalytics.WeekAnalytics;
import com.e104.realtime.domain.WeekAnalytics.WeekWordCloud;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Data
public class WeeklyConversationResponse {

    private List<WeeklyConversation> weeklyConversations = new ArrayList<>();

    private List<WordCloudResponse> wordCloudResponses = new ArrayList<>();

    private String wordCloudSummary; // 대화 워드 클라우드 요약

    private String emotionSummary; // 대화 감정 요약

    private String vocabularySummary; // 대화 어휘 요약
    
    private String countSummary; // 대화 횟수 요약

    @Data
    public static class WeeklyConversation {

        private double vocabularyScore;  // 어휘력 점수

        private int happyScore;  // 행복 지수

        private int loveScore;  // 사랑 지수

        private int sadScore;  // 슬픔 지수

        private int scaryScore;  // 공포 지수

        private int angryScore;  // 화남 지수

        private int amazingScore;  // 놀람 지수

        private int conversationCount;  // 대화 횟수

        private LocalDate createdAt; // 생성일

    }

    @Data
    public static class WordCloudResponse {
        private String word;
        private int count;
    }

    public WeeklyConversationResponse(WeekAnalytics weekAnalytics, List<DayAnalytics> filteredAnalytics) {

        this.wordCloudSummary = weekAnalytics.getWordCloudSummary();
        this.emotionSummary = weekAnalytics.getEmotionSummary();
        this.vocabularySummary = weekAnalytics.getVocabularySummary();
        this.countSummary = weekAnalytics.getCountSummary();

        for (DayAnalytics dayAnalytics : filteredAnalytics) {
            WeeklyConversation weeklyConversation = getWeeklyConversation(dayAnalytics);
            weeklyConversations.add(weeklyConversation);
        }
        weeklyConversations.sort(Comparator.comparing(WeeklyConversation::getCreatedAt));

        for (WeekWordCloud weekWordCloud : weekAnalytics.getWeekWordClouds()) {
            WordCloudResponse wordCloudResponse = new WordCloudResponse();
            wordCloudResponse.setWord(weekWordCloud.getWord());
            wordCloudResponse.setCount(weekWordCloud.getCount());
            wordCloudResponses.add(wordCloudResponse);
        }
    }

    private static WeeklyConversation getWeeklyConversation(DayAnalytics dayAnalytics) {
        WeeklyConversation weeklyConversation = new WeeklyConversation();
        weeklyConversation.setVocabularyScore(dayAnalytics.getVocabularyScore());
        weeklyConversation.setHappyScore(dayAnalytics.getHappyScore());
        weeklyConversation.setLoveScore(dayAnalytics.getLoveScore());
        weeklyConversation.setSadScore(dayAnalytics.getSadScore());
        weeklyConversation.setScaryScore(dayAnalytics.getScaryScore());
        weeklyConversation.setAngryScore(dayAnalytics.getAngryScore());
        weeklyConversation.setAmazingScore(dayAnalytics.getAmazingScore());
        weeklyConversation.setConversationCount(dayAnalytics.getConversationCount());
        weeklyConversation.setCreatedAt(dayAnalytics.getCreatedAt());
        return weeklyConversation;
    }
}
