package com.e104.realtime.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

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
