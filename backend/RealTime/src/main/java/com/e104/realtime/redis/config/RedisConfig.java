package com.e104.realtime.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    // application.properties에서 설정된 Redis 호스트 정보를 주입받음
    @Value("${spring.data.redis.host}")
    private String redisHost;

    // application.properties에서 설정된 Redis 포트 정보를 주입받음
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // RedisConnectionFactory 빈을 생성하는 메서드
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // RedisTemplate 빈을 생성하는 메서드
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // RedisTemplate 객체 생성
        RedisTemplate<String, String> template = new RedisTemplate<>();

        // Redis 연결 팩토리 설정
        template.setConnectionFactory(redisConnectionFactory);

        // 생성된 RedisTemplate 객체 반환
        return template;
    }
}