package com.e104.realtime.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WeeklyConversationResponse {

    private List<WeeklyConversation> sentimentResponses;

    private List<WordCloudResponse> wordCloudResponses = new ArrayList<>();

    private String wordCloudSummary; // 대화 워드 클라우드 요약

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

        private String emotionSummary; // 대화 감정 요약

        private String vocabularySummary; // 대화 어휘 요약
    }

    @Data
    public static class WordCloudResponse {
        private String word;
        private int count;
    }
}
