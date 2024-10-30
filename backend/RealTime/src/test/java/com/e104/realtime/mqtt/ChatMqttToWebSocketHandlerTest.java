package com.e104.realtime.mqtt;

import com.e104.realtime.application.UserService;
import com.e104.realtime.redis.hash.Conversation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ChatMqttToWebSocketHandlerTest {

    private static final String TOPIC_WEBSOCKET_CONNECT = "topic/websocket/connect";
    private static final String TOPIC_MESSAGE_SEND = "topic/message/send";
    private static final String TOPIC_CONVERSATION_END = "topic/conversation/end";
    private static final String TOPIC_USER_DETECTION = "topic/user/detection";
    private static final String TOPIC_VOICE_RECOGNITION = "topic/voice/recognition";

    @Spy
    @InjectMocks
    ChatMqttToWebSocketHandler handler;

    @Mock
    UserService userServiceMock;

    @Mock
    DirectChannel outboundChannel;

    @Test
    void test() throws JsonProcessingException {

        System.out.println(outboundChannel);
        System.out.println(userServiceMock);
        System.out.println(handler);

        GenericMessage<String> message = new GenericMessage<>("{\"userSeq\": 1, \"content\": \"test\"}", new MessageHeaders(Map.of("mqtt_receivedTopic", TOPIC_MESSAGE_SEND)));
        handler.handleMessageFromMqtt(message);

        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        Mockito.verify(userServiceMock).bufferConversation(captor.capture());
        System.out.println(captor.getValue());
    }


}