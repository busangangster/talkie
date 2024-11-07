package com.e104.realtime.application;

import com.e104.realtime.common.response.ResponseDto;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.domain.User.User;
import com.e104.realtime.dto.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // CustomUserDetails에서 사용자 정보를 가져옴
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 응답 데이터 구조 생성
        Map<String, Object> data = new HashMap<>();
        data.put("userSeq", user.getUserSeq());
        data.put("notFirstLogin", user.isNotFirstLogin());

        // ResponseDto를 사용하여 응답 작성
        ResponseEntity<ResponseDto> responseEntity = ResponseDto.response(StatusCode.SUCCESS, data);

        // JSON으로 응답 작성
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
    }
}
