package com.e104.realtime.domain.entity;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WordCloud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wordCloudSeq;  // 단어 클라우드의 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_seq", nullable = false) // 외래키 설정
    private ConversationAnalytics conversationAnalytics;  // 단어 클라우드가 속한 대화

    @Column(nullable = false)
    private String word;  // 단어

    @Column(nullable = false)
    private int count;  // 단어 빈도수

    // 양방향 관계 설정
    public void setConversationAnalytics(ConversationAnalytics conversationAnalytics) {
        try {
            this.conversationAnalytics = conversationAnalytics;  // 대화와 단어 클라우드 간의 양방향 관계 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화와 단어 클라우드 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }
}
