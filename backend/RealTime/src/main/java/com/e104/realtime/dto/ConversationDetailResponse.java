package com.e104.realtime.dto;

import com.e104.realtime.domain.ConversationAnalytics.ConversationAnalytics;
import com.e104.realtime.domain.User.ConversationContent;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class ConversationDetailResponse {

    private int conversationSeq; // 대화 시퀀스

    private String emotionSummary; // 대화 감정 요약

    private String vocabularySummary; // 대화 어휘 요약

    private String wordCloudSummary; // 대화 워드 클라우드 요약

    private int happyScore; // 행복 지수

    private int loveScore; // 사랑 지수

    private int sadScore; // 슬픔 지수

    private int scaryScore; // 공포 지수

    private int angryScore; // 화남 지수

    private int amazingScore; // 놀람 지수

    private List<WordCloudResponse> wordClouds = new ArrayList<>();

    private List<ConversationContentResponse> conversationContents = new ArrayList<>();

    private double vocabularyScore; // 어휘 점수

    @Data
    public static class WordCloudResponse {
        private String word;
        private int count;
    }


    @Data
    public static class ConversationContentResponse {
        private String content;
        private boolean isAnswer;
        private LocalDateTime createdAt;
    }

    public ConversationDetailResponse(ConversationAnalytics conversationAnalytics, List<ConversationContent> contents) {
        this.conversationSeq = conversationAnalytics.getConversationSeq();
        this.emotionSummary = conversationAnalytics.getEmotionSummary();
        this.vocabularySummary = conversationAnalytics.getVocabularySummary();
        this.wordCloudSummary = conversationAnalytics.getWordCloudSummary();
        this.vocabularyScore = conversationAnalytics.getVocabulary().getVocabularyScore();
        this.happyScore = conversationAnalytics.getSentiment().getHappyScore();
        this.loveScore = conversationAnalytics.getSentiment().getLoveScore();
        this.sadScore = conversationAnalytics.getSentiment().getSadScore();
        this.scaryScore = conversationAnalytics.getSentiment().getScaryScore();
        this.angryScore = conversationAnalytics.getSentiment().getAngryScore();
        this.amazingScore = conversationAnalytics.getSentiment().getAmazingScore();

        conversationAnalytics.getWordClouds().forEach(wordCloud -> {
            WordCloudResponse wordCloudResponse = new WordCloudResponse();
            wordCloudResponse.setWord(wordCloud.getWord());
            wordCloudResponse.setCount(wordCloud.getCount());
            wordClouds.add(wordCloudResponse);
        });

        conversationContents.forEach(conversationContent -> {
            ConversationContentResponse conversationContentResponse = new ConversationContentResponse();
            conversationContentResponse.setContent(conversationContent.getContent());
            conversationContentResponse.setAnswer(conversationContent.isAnswer());
            conversationContentResponse.setCreatedAt(conversationContent.getCreatedAt());
            conversationContents.add(conversationContentResponse);
        });
        // createdAt 기준으로 낮은 순으로 정렬
        conversationContents.sort(Comparator.comparing(ConversationContentResponse::getCreatedAt));
    }

}
