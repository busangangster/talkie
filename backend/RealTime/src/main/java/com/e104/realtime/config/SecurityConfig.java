package com.e104.realtime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 활성화
                .formLogin(form -> form
                        .loginPage("https://k11e104.p.ssafy.io/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/api/loginOK")
//                        .successHandler(((request, response, authentication) -> response.sendRedirect("https://k11e104.p.ssafy.io")))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
//                        .logoutSuccessHandler(((request, response, authentication) -> response.sendRedirect("https://k11e104.p.ssafy.io/login")))
                        .logoutSuccessUrl("/api/logoutOK")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true).deleteCookies("JSESSIONID")
                )
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/login").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(auth -> auth
                        .sessionFixation().changeSessionId()
                        .maximumSessions(1).maxSessionsPreventsLogin(true));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173", "https://k11e104.p.ssafy.io", "http://k11e104.p.ssafy.io"));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://k11e104.p.ssafy.io", "http://k11e104.p.ssafy.io")); // 허용할 도메인 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 쿠키 및 인증 헤더 전송 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}