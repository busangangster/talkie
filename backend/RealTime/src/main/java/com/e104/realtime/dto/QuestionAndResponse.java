package com.e104.realtime.dto;

import com.e104.realtime.domain.User.Question;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestionAndResponse {

    private int questionSeq;

    private String question;

    private LocalDateTime questionCreatedAt;

    private boolean questionIsActive;

    private String answer;

    private LocalDateTime answerCreatedAt;

    // Question 객체를 받아서 필드 초기화하는 생성자 추가
    public QuestionAndResponse(Question question) {
        this.questionSeq = question.getQuestionSeq();
        this.question = question.getContent();
        this.questionCreatedAt = question.getCreatedAt();
        this.questionIsActive = question.isActive();

        // Answer가 존재하는 경우에만 값 설정
        if (question.getAnswer() != null) {
            this.answer = question.getAnswer().getContent();
            this.answerCreatedAt = question.getAnswer().getCreatedAt();
        }
    }
}
