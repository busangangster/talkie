package com.e104.realtime.mqtt;

import com.e104.realtime.application.UserService;
import com.e104.realtime.mqtt.constant.Topic;
import com.e104.realtime.redis.hash.Conversation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ChatMqttToWebSocketHandlerTest {

    @Spy
    @InjectMocks
    ChatMqttToWebSocketHandler handler;

    @Mock
    UserService userServiceMock;

    @Mock
    DirectChannel outboundChannel;

    @Deprecated
    @Disabled("헤더 분기 사용 안 해서 더 이상 필요가 없는 테스트.")
    @Test
    void mqttMessageSendTopicTest() {
        GenericMessage<String> message = new GenericMessage<>("{\"userSeq\": 1, \"content\": \"test\"}", new MessageHeaders(Map.of("mqtt_receivedTopic", Topic.TOPIC_MESSAGE_SEND)));
        handler.handleMessageFromMqtt(message);

        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        Mockito.verify(userServiceMock).bufferConversation(captor.capture());

        assertEquals("test", captor.getValue().getContent());
    }

}