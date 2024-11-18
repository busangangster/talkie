package com.e104.realtime.domain.DayAnalytics;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Slf4j
public class DayWordCloud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dayWordCloudSeq; // 일별 워드 클라우드의 고유 식별자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daySeq", nullable = false) // 외래키 설정, 사용자와의 관계를 나타냄
    private DayAnalytics dayAnalytics; // 다대일 관계로 연결된 DayAnalytics 엔티티, 일별 워드 클라우드는 하루 분석 정보에 속함

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private int count;

    @Column
    private LocalDate createdAt; // YYYY-MM-DD 형식의 생성 시간

    // 양방향 관계 설정
    public void setDayAnalytics(DayAnalytics dayAnalytics) {
        try {
            this.dayAnalytics = dayAnalytics; // 일별 워드 클라우드와 일별 분석 간의 양방향 관계 설정
        } catch (Exception e) {
            log.error("일별 워드 클라우드와 일별 분석 간의 관계 설정 중 오류가 발생했습니다.", e);
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "일별 워드 클라우드와 일별 분석 간의 관계 설정 중 오류가 발생했습니다.");
        }
    }

}
