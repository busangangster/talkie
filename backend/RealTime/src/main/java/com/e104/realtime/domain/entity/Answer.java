package com.e104.realtime.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int answerSeq;  // 답변의 고유 식별자

    // 양방향 관계 설정을 위한 메서드
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_seq", nullable = false) // 외래키 설정, 영화와의 관계를 나타냄
    private Question question;

    @Column(nullable = false)
    private String content;  // 답변 내용

    @Column(nullable = false)
    private LocalDateTime createdAt;  // 답변 생성 시간

    // 답변 생성 시간 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 양방향 관계 설정을 위한 메서드
    public void setQuestion(Question question) {
        try {
            this.question = question; // 질문과 답변 간의 양방향 관계 설정
        } catch (Exception e) {
            throw new RuntimeException("질문과 답변 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }
}
