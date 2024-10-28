package com.e104.realtime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // 스프링 부트의 설정 클래스를 나타냅니다.
public class AppConfig {

    /**
     * RestTemplate 빈을 생성합니다.
     * RestTemplate은 동기식 HTTP 요청을 보낼 때 사용됩니다.
     * 이 빈은 애플리케이션의 다른 부분에서 주입 받아 사용할 수 있습니다.
     *
     * @return 새로운 RestTemplate 객체
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
