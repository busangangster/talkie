package com.e104.realtime.application;

import com.e104.realtime.common.response.ResponseDto;
import com.e104.realtime.common.status.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        ResponseEntity<ResponseDto> responseEntity = ResponseDto.response(StatusCode.SUCCESS, "로그아웃 성공");

        // JSON으로 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }
}
