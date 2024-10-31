package com.e104.realtime.domain.WeekAnalytics;


import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WeekWordCloud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int weekWordCloudSeq;

    @ManyToOne
    @JoinColumn(name = "week_seq", nullable = false)
    private WeekAnalytics weekAnalytics;

    @Column(nullable = false)
    private String word;

    @Column(nullable = false)
    private int count;


    // 양방향 관계 설정
    public void setWeekAnalytics(WeekAnalytics weekAnalytics) {
        try {
            this.weekAnalytics = weekAnalytics;
        } catch (NullPointerException e) {
            throw new RestApiException(StatusCode.INTERNAL_SERVER_ERROR, "주간 워드 클라우드에 주간 분석이 연결되어 있지 않습니다.");
        }
    }

}
