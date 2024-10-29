package com.e104.realtime.application;

import com.e104.realtime.config.KafkaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomKafkaConsumer {

    private final ObjectMapper objectMapper;
//    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "randomTopic")
    @SendTo // 다시 카ㅡㅍ카로 멧지ㅣ 봰느 게 됨 저거 ㅗvoid 바ㅜㄱ머 ㄴ됨 ㅇㅇㅇ
    public void handleRandomTopic(String randomTopic) {
        // doSomething.
    }

}
