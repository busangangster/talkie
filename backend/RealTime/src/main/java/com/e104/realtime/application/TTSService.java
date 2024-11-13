package com.e104.realtime.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TTSService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OPENAI_TTS_URL = "https://api.openai.com/v1/audio/speech";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] getPcmAudio(String text) {
        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 데이터 설정
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "tts-1-hd");
            requestData.put("voice", "echo");
            requestData.put("input", text);
            requestData.put("response_format", "pcm");
            requestData.put("speed", 1);

            // JSON 직렬화
            String requestJson = objectMapper.writeValueAsString(requestData);

            // HTTP 요청 전송
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    OPENAI_TTS_URL, HttpMethod.POST, entity, byte[].class
            );

            // PCM 데이터 반환
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody(); // PCM 데이터 반환
            } else {
                throw new RuntimeException("Failed to generate PCM audio, status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.info("TTS 요청 중 문제가 발생했습니다.", e);
            throw new RuntimeException("Failed to generate PCM audio", e);
        }
    }
}
