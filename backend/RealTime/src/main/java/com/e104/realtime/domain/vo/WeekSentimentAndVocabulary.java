package com.e104.realtime.domain.vo;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WeekSentimentAndVocabulary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int weekSvSeq;

    @ManyToOne
    @JoinColumn(name = "week_seq", nullable = false)
    private WeekAnalytics weekAnalytics;

    @Column(nullable = false)
    private double vocabularyScore;

    @Column(nullable = false)
    private int happyScore;

    @Column(nullable = false)
    private int loveScore;

    @Column(nullable = false)
    private int sadScore;

    @Column(nullable = false)
    private int scaryScore;

    @Column(nullable = false)
    private int angryScore;

    @Column(nullable = false)
    private int amazingScore;

    @Column(nullable = false)
    private int conversationCount;

    @Column(nullable = false)
    private LocalDate createdAt;

    // 양방향 관계 설정
    public void setWeekAnalytics(WeekAnalytics weekAnalytics) {
        try {
            this.weekAnalytics = weekAnalytics;
        } catch (Exception e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "WeekSentimentAndVocabulary setWeekAnalytics error");
        }
    }
}
