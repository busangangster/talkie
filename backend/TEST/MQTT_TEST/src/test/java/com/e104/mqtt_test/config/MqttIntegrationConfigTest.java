package com.e104.mqtt_test.config;

import com.e104.mqtt_test.service.ChatMqttToWebSocketHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.support.GenericMessage;

import java.util.Objects;

@SpringBootTest
@SpringIntegrationTest(noAutoStartup = {"inboundAdapter"})
class MqttIntegrationConfigTest {

    @Autowired
    @Qualifier("inboundAdapter")
    MessageProducer inbound;

    @MockBean
    ChatMqttToWebSocketHandler handler;

    @DisplayName("MQTT 입력이 잘 작동하는지 확인한다.")
    @Test
    void mqttChannelTest() {
        var message = new GenericMessage<>("테스트!");
        Mockito.doNothing().when(handler).handleMessageFromMqtt(Mockito.any());
        Objects.requireNonNull(inbound.getOutputChannel()).send(message);
        Mockito.verify(handler, Mockito.times(1)).handleMessageFromMqtt(Mockito.any());
    }

}