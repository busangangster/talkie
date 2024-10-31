package com.e104.realtime.domain.entity;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userSeq;  // 사용자의 고유 식별자, 자동으로 생성됨

    @Column(nullable = false)
    private String userId;  // 사용자 ID

    @Column
    private String name; // 사용자 이름

    @Column
    private Integer age; // 사용자 나이

    @Column
    private String gender; // 사용자 성별 ( M / F )

    @Column
    private String favorite; // 사용자의 관심사

    @Column(nullable = false)
    private boolean isNotFirstLogin;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private final List<Question> questions = new ArrayList<>(); // 사용자가 작성한 질문 리스트, 사용자와 양방향 관계를 설정하며, 사용자가 삭제되면 질문도 함께 삭제됨 (CascadeType.ALL)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private final List<DayAnalytics> dayAnalytics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private final List<ConversationAnalytics> conversationAnalytics  = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private final List<WeekAnalytics> weekAnalytics = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private final List<ConversationContent> conversationContents = new ArrayList<>();

    // 사용자 정보 변경 메서드
    @Transactional
    public void updateUserInfo(String newName, Integer newAge, String newGender, String newFavorite) {
        try {
            this.name = newName;
            this.age = newAge;
            this.gender = newGender;
            this.favorite = newFavorite;
            this.isNotFirstLogin = true;
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "사용자 정보 업데이트 중 오류가 발생했습니다.");
        }
    }

    // 질문 조회
    public Question getQuestion(int questionSeq) {
        return this.questions.stream()
                .filter(question -> question.getQuestionSeq() == questionSeq)
                .findFirst()
                .orElseThrow(() -> new RestApiException(StatusCode.NOT_FOUND, "해당 질문을 찾을 수 없습니다."));
    }

    // 질문추가
    @Transactional
    public void addQuestion(Question question) {
        try {
            if(questions.get(questions.size()-1).isActive()) {
                throw new RestApiException(StatusCode.BAD_REQUEST, "이미 활성화된 질문이 있습니다.");
            }
            question.setUser(this);  // 양방향 관계 설정 (Actor 객체가 이 영화에 속해 있음을 명시)
            this.questions.add(question);  // 질문리스트에 새로운 질문 추가
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "질문 추가 중 오류가 발생했습니다.");
        }
    }

    // 질문 삭제
    @Transactional
    public void removeQuestion(int questionSeq) {
        try {
            Question removeQuestion = this.getQuestion(questionSeq);
            if(!removeQuestion.isActive()) {
                throw new RestApiException(StatusCode.BAD_REQUEST, "대답이 완료된 질문은 삭제할 수 없습니다.");
            }
            removeQuestion.setUser(null);  // 양방향 관계 해제
            this.questions.remove(removeQuestion);
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "질문 제거 중 오류가 발생했습니다.");
        }
    }

    // 일별 통계 추가
    @Transactional
    public void addDayAnalytics(DayAnalytics dayAnalytics) {
        try {
            dayAnalytics.setUser(this);
            this.dayAnalytics.add(dayAnalytics);
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "일별 통계 추가 중 오류가 발생했습니다.");
        }
    }

    // 대화 조회
    public ConversationAnalytics getConversationAnalytics(int conversationSeq) {
        return this.conversationAnalytics.stream()
                .filter(conversation -> conversation.getConversationSeq() == conversationSeq)
                .findFirst()
                .orElseThrow(() -> new RestApiException(StatusCode.NOT_FOUND, "해당 대화를 찾을 수 없습니다."));
    }

    // 대화별 통계 추가
    @Transactional
    public void addConversationAnalytics(ConversationAnalytics conversationAnalytics) {
        try {
            conversationAnalytics.setUser(this);
            this.conversationAnalytics.add(conversationAnalytics);
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화별 통계 추가 중 오류가 발생했습니다.");
        }
    }

    // 주별 통계 추가
    @Transactional
    public void addWeekAnalytics(WeekAnalytics weekAnalytics) {
        try {
            weekAnalytics.setUser(this);
            this.weekAnalytics.add(weekAnalytics);
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "주별 통계 추가 중 오류가 발생했습니다.");
        }
    }
    
    // 대화 내용 추가
    @Transactional
    public void addConversationContents(List<ConversationContent> conversationContents) {
        try {
            conversationContents.forEach(e -> e.setUser(this));
            this.conversationContents.addAll(conversationContents);
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "대화 내용 추가 중 오류가 발생했습니다.");
        }
    }
}
