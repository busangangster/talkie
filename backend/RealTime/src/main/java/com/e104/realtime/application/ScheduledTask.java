package com.e104.realtime.application;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling  // 스케줄링 활성화
@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final RepoUtil repoUtil;
    private final BuilderUtil builderUtil;
    private final ChatService chatService;

    // 매일 밤 11시에 실행되도록 설정 (24시간 기준)
    @Scheduled(cron = "0 0 23 * * ?", zone = "Asia/Seoul")
    public void runTask() {
        System.out.println("매일 밤 11시에 실행되는 작업입니다.");
        // 작업 로직을 여기에 추가합니다.
        // TODO: 스케줄러 ( 일별 대화 통계 저장 , Kafka 구독 및 FastAPI, GPT 호출 [제목, 요약, 감정분석/워드클라우드/어휘력 설명] )
    }
}
