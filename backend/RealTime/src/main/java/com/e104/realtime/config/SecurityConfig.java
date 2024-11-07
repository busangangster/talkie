package com.e104.realtime.config;

import com.e104.realtime.application.CustomLoginSuccessHandler;
import com.e104.realtime.application.CustomLogoutSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomLoginSuccessHandler customLoginSuccessHandler, CustomLogoutSuccessHandler customLogoutSuccessHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 활성화
                .formLogin(form -> form
                        .loginPage("https://k11e104.p.ssafy.io/login")
                        .loginProcessingUrl("/api/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                )
                .authorizeHttpRequests(req -> req
                                .requestMatchers("/api/login", "/api/logoutOK").permitAll()
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )
                .sessionManagement(auth -> auth
                        .sessionFixation().changeSessionId()
                        .maximumSessions(1).maxSessionsPreventsLogin(true)
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
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
