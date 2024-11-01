package com.e104.realtime.domain.User;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionSeq;  // 질문의 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false) // 외래키 설정, 영화와의 관계를 나타냄
    private User user;

    @Column(nullable = false)
    private String content;  // 질문 내용

    @Column(nullable = false)
    private boolean isActive;  // 질문 활성화 여부

    @Column(nullable = false)
    private boolean isAnswered;  // 질문에 대한 답변이 있는지 여부

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 질문 생성 시간

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private Answer answer;  // 질문에 대한 답변


    // 질문 생성 시간 설정
    @PrePersist
    public void prePersist() {
        this.isActive = true;  // 질문이 생성되면 기본적으로 활성화됨
        this.isAnswered = false;  // 질문에 대한 답변이 없음
        this.createdAt = LocalDateTime.now();
    }

    // 대답 추가
    @Transactional
    public void addAnswer(Answer answer) {
        try {
            answer.setQuestion(this);  // 양방향 관계 설정 (Answer 객체가 이 질문에 속해 있음을 명시)
            this.answer = answer;  // 질문에 대한 답변 추가
            this.isActive = false;  // 질문 활성화 여부를 false로 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "답변 추가 중 오류가 발생했습니다.");
        }
    }


    // 양방향 관계 설정을 위한 메서드
    public void setUser(User user) {
        try {
            this.user = user;
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "유저와 질문 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void updateQuestion(String content) {
        try {
            if (!this.isActive) {
                throw new RestApiException(StatusCode.BAD_REQUEST, "이미 답변이 달린 질문은 수정할 수 없습니다.");
            }
            this.content = content;
            this.createdAt = LocalDateTime.now();
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "질문 정보 업데이트 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public void updateAnswerd(boolean isAnswered) {
        try {
            this.isAnswered = isAnswered;
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "질문 답변 여부 업데이트 중 오류가 발생했습니다.");
        }
    }
}
