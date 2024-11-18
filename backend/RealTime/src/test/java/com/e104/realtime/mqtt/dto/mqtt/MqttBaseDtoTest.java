package com.e104.realtime.mqtt.dto.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class MqttBaseDtoTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void genericConvertTest() throws JsonProcessingException {
        String plain = "{\"userSeq\": 1, \"header\": \"test\", \"data\": {\"a\":1231313}}";
        MqttBaseDto dto = objectMapper.readValue(plain, MqttBaseDto.class);
        System.out.println(dto);
    }

    @Test
    void genericConvertWithoutDataTest() throws JsonProcessingException {
        String plain = "{\"userSeq\": 1, \"header\": \"test\"}";
        MqttBaseDto dto = objectMapper.readValue(plain, MqttBaseDto.class);
        System.out.println(dto);
    }

}