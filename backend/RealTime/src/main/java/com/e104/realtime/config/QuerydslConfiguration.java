package com.e104.realtime.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration  // 이 클래스가 Spring의 설정 클래스임을 나타냅니다.
public class QuerydslConfiguration {

    @PersistenceContext  // EntityManager를 주입받기 위해 사용합니다.
    private EntityManager entityManager;

    @Bean  // 이 메서드가 Spring Bean으로 등록됨을 나타냅니다.
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);  // JPAQueryFactory를 생성하여 반환합니다.
    }
}
