package com.e104.realtime.domain.WeekAnalytics;

import com.e104.realtime.domain.User.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

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
    private final List<WeekWordCloud> weekWordClouds = new ArrayList<>();

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int week;

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

    // 주간 워드 클라우드 초기화
    @Transactional
    public void clearWordClouds() {
        try {
            for(WeekWordCloud weekWordCloud : weekWordClouds) {
                weekWordCloud.setWeekAnalytics(null);
            }
            weekWordClouds.clear();
        } catch (Exception e) {
            throw new RuntimeException("주간 워드 클라우드 초기화에 실패했습니다.");
        }
    }
}
