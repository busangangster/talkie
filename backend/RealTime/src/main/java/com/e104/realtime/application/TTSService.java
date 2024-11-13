package com.e104.realtime.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class TTSService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OPENAI_TTS_URL = "https://api.openai.com/v1/audio/speech";

    public byte[] getPcmAudio(String text) {
        try {
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 요청 데이터 설정
            String requestJson = String.format(
                    "{" +
                            "\"model\": \"tts-1-hd\"," +
                            "\"voice\": \"echo\"," +
                            "\"input\": \"%s\"," +
                            "\"response_format\": \"pcm\"," +
                            "\"speed\": 1" +
                            "}", text
            );

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
