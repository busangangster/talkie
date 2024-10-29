package com.e104.realtime.domain.vo;

import com.e104.realtime.domain.entity.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WeekAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int weekSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false) // 외래키 설정
    private User user;

    @Column(nullable = false)
    private String emotionSummary;

    @Column(nullable = false)
    private String vocabularySummary;

    @Column(nullable = false)
    private String wordCloudSummary;

    @OneToMany(mappedBy = "weekAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private List<WeekWordCloud> weekWordClouds = new ArrayList<>();

    @OneToMany(mappedBy = "weekAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private List<WeekSentimentAndVocabulary> weekSentimentAndVocabularies = new ArrayList<>();

    @Column
    private LocalDate createdAt;


    // 양방향 관계 설정
    public void setUser(User user) {
        this.user = user;
        user.getWeekAnalytics().add(this);
    }

    // 요약 업데이트
    @Transactional
    public void updateSummary(String emotionSummary, String vocabularySummary, String wordCloudSummary) {
        this.emotionSummary = emotionSummary;
        this.vocabularySummary = vocabularySummary;
        this.wordCloudSummary = wordCloudSummary;
    }

    // 주간 워드 클라우드 추가
    @Transactional
    public void addWordClouds(List<WeekWordCloud> weekWordClouds) {
        try {
            for(WeekWordCloud weekWordCloud : weekWordClouds) {
                weekWordCloud.setWeekAnalytics(this);
                this.weekWordClouds.add(weekWordCloud);
            }
        } catch (Exception e) {
            throw new RuntimeException("주간 워드 클라우드 추가에 실패했습니다.");
        }
    }

    // 주간 감정 및 어휘 추가
    @Transactional
    public void addSentimentAndVocabularies(List<WeekSentimentAndVocabulary> weekSentimentAndVocabularies) {
        try {
            for(WeekSentimentAndVocabulary weekSentimentAndVocabulary : weekSentimentAndVocabularies) {
                weekSentimentAndVocabulary.setWeekAnalytics(this);
                this.weekSentimentAndVocabularies.add(weekSentimentAndVocabulary);
            }
        } catch (Exception e) {
            throw new RuntimeException("주간 감정 및 어휘 추가에 실패했습니다.");
        }
    }
}
