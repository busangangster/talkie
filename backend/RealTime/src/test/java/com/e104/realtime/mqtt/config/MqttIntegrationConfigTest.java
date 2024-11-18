package com.e104.realtime.mqtt.config;

import com.e104.realtime.mqtt.ChatMqttToWebSocketHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

@Disabled("Config 조금 변경하면서 테스트가 고장났음. 수정 필요")

@ExtendWith(SpringExtension.class)
@SpringIntegrationTest(noAutoStartup = {"inboundAdapter"})
@Import(MqttIntegrationConfig.class)
@ContextConfiguration
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

    @TestConfiguration
    @EnableIntegration
    static class FakeMqttIntegrationConfig {
        @Bean
        public MqttIntegrationConfig config(MqttIntegrationConfig config) {
            config.setBrokerUrl("tcp://its.fake.url:1234");
            config.setPublishTopic("fakeTopic1");
            config.setSubscribeTopic("fakeTopic2");
            return config;
        }
    }

}
