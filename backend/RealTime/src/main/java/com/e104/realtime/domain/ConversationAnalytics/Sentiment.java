package com.e104.realtime.domain.ConversationAnalytics;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Sentiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sentimentSeq;  // 감정의 고유 식별자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_seq", nullable = false) // 외래키 설정
    private ConversationAnalytics conversationAnalytics;

    @Column(nullable = false)
    private int happyScore;  // 행복 지수

    @Column(nullable = false)
    private int loveScore;  // 사랑 지수

    @Column(nullable = false)
    private int sadScore;  // 슬픔 지수

    @Column(nullable = false)
    private int scaryScore;  // 공포 지수

    @Column(nullable = false)
    private int angryScore;  // 화남 지수

    @Column(nullable = false)
    private int amazingScore;  // 놀람 지수

    // 양방향 관계 설정
    public void setConversationAnalytics(ConversationAnalytics conversationAnalytics) {
        try {
            this.conversationAnalytics = conversationAnalytics; // 대화와 감정 간의 양방향 관계 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화와 감정 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }

}
