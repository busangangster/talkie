package com.e104.realtime.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")
//                .allowedOrigins("https://k11e104.p.ssafy.io", "http://k11e104.p.ssafy.io", "http://localhost:5173") // 여러 도메인 허용
//                .allowedMethods("POST", "PUT", "GET", "DELETE")
//                .allowCredentials(true); // 자격 증명 허용
//    }
}
