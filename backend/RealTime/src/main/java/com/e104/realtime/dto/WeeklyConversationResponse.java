package com.e104.realtime.dto;

import com.e104.realtime.domain.vo.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WeeklyConversationResponse {

    private List<WeeklyConversation> weeklyConversations = new ArrayList<>();

    private List<WordCloudResponse> wordCloudResponses = new ArrayList<>();

    private String wordCloudSummary; // 대화 워드 클라우드 요약

    private String emotionSummary; // 대화 감정 요약

    private String vocabularySummary; // 대화 어휘 요약

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

        private LocalDateTime createdAt; // 생성일

    }

    @Data
    public static class WordCloudResponse {
        private String word;
        private int count;
    }

    public WeeklyConversationResponse(List<DayAnalytics> list, String emotionSummary, String vocabularySummary, String wordCloudSummary) {
        for (DayAnalytics conversation : list) {
            WeeklyConversation weeklyConversation = getWeeklyConversation(conversation);
            weeklyConversations.add(weeklyConversation);

            WordCloudResponse wordCloudResponse = new WordCloudResponse();
            for(DayWordCloud dayWordCloud : conversation.getDayWordClouds()){
                wordCloudResponse.setWord(dayWordCloud.getWord());
                wordCloudResponse.setCount(dayWordCloud.getCount());
                wordCloudResponses.add(wordCloudResponse);
            }
        }
        this.emotionSummary = emotionSummary;
        this.vocabularySummary = vocabularySummary;
        this.wordCloudSummary = wordCloudSummary;
    }

    private static WeeklyConversation getWeeklyConversation(DayAnalytics conversation) {
        WeeklyConversation weeklyConversation = new WeeklyConversation();
        weeklyConversation.setVocabularyScore(conversation.getVocabularyScore());
        weeklyConversation.setHappyScore(conversation.getHappyScore());
        weeklyConversation.setLoveScore(conversation.getLoveScore());
        weeklyConversation.setSadScore(conversation.getSadScore());
        weeklyConversation.setScaryScore(conversation.getScaryScore());
        weeklyConversation.setAngryScore(conversation.getAngryScore());
        weeklyConversation.setAmazingScore(conversation.getAmazingScore());
        weeklyConversation.setConversationCount(conversation.getConversationCount());
        weeklyConversation.setCreatedAt(conversation.getCreatedAt());
        return weeklyConversation;
    }
}
