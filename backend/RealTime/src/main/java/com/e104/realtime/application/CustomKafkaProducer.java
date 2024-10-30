package com.e104.realtime.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomKafkaProducer {

    private final ObjectMapper objectMapper;  // Jackson ObjectMapper로 JSON 직렬화
    private final KafkaTemplate<String, String> kafkaTemplate;

    public <T> void send(String topic, T event) throws JsonProcessingException {
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
    }

}
