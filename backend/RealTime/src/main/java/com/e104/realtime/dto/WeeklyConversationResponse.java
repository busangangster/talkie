package com.e104.realtime.dto;

import com.e104.realtime.domain.vo.WeekAnalytics;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//TODO: 수정
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

        private LocalDate createdAt; // 생성일

    }

    @Data
    public static class WordCloudResponse {
        private String word;
        private int count;
    }

    public WeeklyConversationResponse(WeekAnalytics weekAnalytics) {

        for(WeekAnalytics weekAnalytics : list) {
            WeeklyConversation weeklyConversation = new WeeklyConversation();
            weeklyConversation.setVocabularyScore(weekAnalytics.getVocabularyScore());
            weeklyConversation.setHappyScore(weekAnalytics.getHappyScore());
            weeklyConversation.setLoveScore(weekAnalytics.getLoveScore());
            weeklyConversation.setSadScore(weekAnalytics.getSadScore());
            weeklyConversation.setScaryScore(weekAnalytics.getScaryScore());
            weeklyConversation.setAngryScore(weekAnalytics.getAngryScore());
            weeklyConversation.setAmazingScore(weekAnalytics.getAmazingScore());
            weeklyConversation.setConversationCount(weekAnalytics.getConversationCount());
            weeklyConversation.setCreatedAt(weekAnalytics.getCreatedAt());
            weeklyConversations.add(weeklyConversation);
        }

    }

}
