package com.e104.realtime.application;

import com.e104.realtime.common.exception.RestApiException;
import com.e104.realtime.common.status.StatusCode;
import com.e104.realtime.config.KafkaConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

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
