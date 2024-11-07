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
public class ConversationSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conversationSummarySeq;  // 대화 요약의 고유 식별자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_seq", nullable = false) // 외래키 설정, 대화와의 관계를 나타냄
    private ConversationAnalytics conversationAnalytics;  // 대화 요약이 속한 대화

    @Column(length = 3000)
    private String content; // 대화 요약 내용

    // 양방향 관계 설정
    public void setConversationAnalytics(ConversationAnalytics conversationAnalytics) {
        try {
            this.conversationAnalytics = conversationAnalytics;  // 대화 요약과 대화 간의 양방향 관계 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화 요약과 대화 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }
}
