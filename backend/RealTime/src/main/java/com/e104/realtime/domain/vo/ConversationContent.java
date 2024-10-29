package com.e104.realtime.domain.vo;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConversationContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conversationContentSeq;  // 대화 내용의 고유 식별자

//    @ManyToOne
//    @JoinColumn(name = "conversation_seq", nullable = false) // 외래키 설정, 대화와의 관계를 나타냄
//    private ConversationAnalytics conversationAnalytics; // 다대일 관계로 연결된 Conversation 엔티티, 대화 내용은 한 대화에만 속할 수 있음

    @ManyToOne
    @JoinColumn(name = "user_seq")
    private User user;

    @Column(nullable = false)
    private String content; // 대화 내용

    @Column(nullable = false)
    private boolean isAnswer; // True: 답변, False: 질문

    @Column(nullable = false)
    private LocalDateTime createdAt; // 대화 내용 생성 시간

    // 생성 시간 설정
    @PrePersist
    public void prePersist() {
        try {
            this.createdAt = LocalDateTime.now(); // 대화 내용이 생성되면 현재 시간으로 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화 내용 생성 시간 설정 중 오류가 발생했습니다.");
        }
    }

//    // 양방향 관계 설정
//    public void setConversationAnalytics(ConversationAnalytics conversationAnalytics) {
//        try {
//            this.conversationAnalytics = conversationAnalytics; // 대화 내용과 대화 간의 양방향 관계 설정
//        } catch (Exception e) {
//            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화 내용과 대화 간의 관계 설정 중 오류가 발생했습니다.");
//        }
//    }
}
