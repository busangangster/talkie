package com.e104.realtime.domain.entity;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
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
public class DayAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int daySeq;  // 일별 분석의 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_seq", nullable = false) // 외래키 설정, 영화와의 관계를 나타냄
    private User user;

    @Column(nullable = false)
    private double vocabularyScore;  // 어휘력 점수

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

    @Column(nullable = false)
    private int conversationCount;  // 대화 횟수

    @Column(nullable = false)
    private LocalDate createdAt; // YYYY-MM-DD 형식의 생성 시간

    @OneToMany(mappedBy = "dayAnalytics", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column
    private final List<DayWordCloud> dayWordClouds = new ArrayList<>(); // 일별 워드 클라우드 리스트, 일별 분석과 양방향 관계를 설정하며, 일별 분석이 삭제되면 워드 클라우드도 함께 삭제됨 (CascadeType.ALL)

    // 날짜 생성
    @PrePersist
    public void prePersist() {
        try {
            this.createdAt = LocalDate.now(); // 현재 시간으로 날짜 생성
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "날짜 생성 중 오류가 발생했습니다.");
        }
    }

    // 양방향 관계 설정
    public void setUser(User user) {
        try {
            this.user = user; // 사용자와 일별 분석 간의 양방향 관계 설정
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "사용자와 일별 분석 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }

    // 워드클라우드 추가
    @Transactional
    public void addDayWordClouds(List<DayWordCloud> dayWordClouds) {
        try {
            for (DayWordCloud dayWordCloud : dayWordClouds) {
                dayWordCloud.setDayAnalytics(this); // 양방향 관계 설정 (DayWordCloud 객체가 이 일별 분석에 속해 있음을 명시)
                this.dayWordClouds.add(dayWordCloud); // 일별 분석에 워드 클라우드 추가
            }
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "워드 클라우드 추가 중 오류가 발생했습니다.");
        }
    }
}
