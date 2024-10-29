package com.e104.realtime.domain.vo;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.entity.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConversationAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conversationSeq;  // 대화의 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false) // 외래키 설정, 영화와의 관계를 나타냄
    private User user;

    @Column(nullable = false)
    private String title; // 대화 제목

    @Column(nullable = false)
    private String emotionSummary; // 대화 감정 요약

    @Column(nullable = false)
    private String vocabularySummary; // 대화 어휘 요약

    @Column(nullable = false)
    private String wordCloudSummary; // 대화 워드 클라우드 요약

    @Column(nullable = false)
    private LocalDateTime createdAt; // 대화 생성 시간

    @OneToOne(mappedBy = "conversationAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private Sentiment sentiment; // 대화에 대한 감정 리스트, 대화와 양방향 관계를 설정하며, 대화가 삭제되면 감정도 함께 삭제됨 (CascadeType.ALL)

    @OneToOne(mappedBy = "conversationAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private Vocabulary vocabulary;

    @OneToMany(mappedBy = "conversationAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private List<WordCloud> wordClouds = new ArrayList<>();  // 대화에 대한 단어 클라우드 리스트, 대화와 양방향 관계를 설정하며, 대화가 삭제되면 단어 클라우드도 함께 삭제됨 (CascadeType.ALL)

    @OneToMany(mappedBy = "conversationAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private List<ConversationContent> conversationContents = new ArrayList<>();  // 대화에 대한 대화 내용 리스트, 대화와 양방향 관계를 설정하며, 대화가 삭제되면 대화 내용도 함께 삭제됨 (CascadeType.ALL)

    @OneToOne(mappedBy = "conversationAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private ConversationSummary conversationSummary;

    // 양방향 관계 설정
    public void setUser(User user) {
        try {
            this.user = user; // 사용자와 대화 간의 양방향 관계 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "사용자와 대화 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }

    // 감성 점수 추가
    @Transactional
    public void addSentiment(Sentiment sentiment) {
        try {
            sentiment.setConversationAnalytics(this);  // 양방향 관계 설정 (Sentiment 객체가 이 대화에 속해 있음을 명시)
            this.sentiment = sentiment;  // 감정 정보 추가
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "감정 추가 중 오류가 발생했습니다.");
        }
    }

    // 어휘력 점수 추가
    @Transactional
    public void addVocabulary(Vocabulary vocabulary) {
        try {
            vocabulary.setConversationAnalytics(this);  // 양방향 관계 설정 (Vocabulary 객체가 이 대화에 속해 있음을 명시)
            this.vocabulary = vocabulary;  // 어휘 정보 추가
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "어휘 추가 중 오류가 발생했습니다.");
        }
    }

    // 워드클라우드 추가
    @Transactional
    public void addWordCloud(List<WordCloud> wordCloud) {
        try {
            for(WordCloud wc : wordCloud) {
                wc.setConversationAnalytics(this);  // 양방향 관계 설정 (WordCloud 객체가 이 대화에 속해 있음을 명시)
                this.wordClouds.add(wc);  // 워드클라우드 리스트에 새로운 워드클라우드 추가
            }
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "워드클라우드 추가 중 오류가 발생했습니다.");
        }
    }

    // 대화 내용 추가
    @Transactional
    public void addConversationContent(List<ConversationContent> conversationContents) {
        try {
            for(ConversationContent cc : conversationContents) {
                cc.setConversationAnalytics(this);  // 양방향 관계 설정 (ConversationContent 객체가 이 대화에 속해 있음을 명시)
                this.conversationContents.add(cc);  // 대화 내용 리스트에 새로운 대화 내용 추가
            }
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화 내용 추가 중 오류가 발생했습니다.");
        }
    }

    // 대화 내용 요약 추가
    @Transactional
    public void addConversationSummary(ConversationSummary conversationSummary) {
        try {
            conversationSummary.setConversationAnalytics(this);  // 양방향 관계 설정 (ConversationSummary 객체가 이 대화에 속해 있음을 명시)
            this.conversationSummary = conversationSummary;  // 대화 요약 추가
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화 요약 추가 중 오류가 발생했습니다.");
        }
    }
}
