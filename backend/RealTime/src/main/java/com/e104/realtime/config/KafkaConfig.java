package com.e104.realtime.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Data
public class KafkaConfig {

    private String bootstrapServers; // 카프카 서버 주소
    private Producer producer; // 프로듀서 설정
    private Consumer consumer; // 컨슈머 설정
    private Template template; // 템플릿 설정

    @Data
    public static class Producer {
        private String keySerializer; // 키 직렬화
        private String valueSerializer; // 값 직렬화
    }

    @Data
    public static class Consumer {
        private String groupId; // 그룹 ID
        private String enableAutoCommit; // 자동 커밋 활성화
        private String autoOffsetReset; // 오프셋 리셋
        private String keyDeserializer; // 키 역직렬화
        private String valueDeserializer; // 값 역직렬화
        private String maxPollRecords; // 최대 폴 레코드
    }

    @Data
    public static class Template {
        private String movieInfoTopic; // 영화 정보 토픽
    }
}
