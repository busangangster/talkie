package com.e104.realtime.mqtt.config;

import com.e104.realtime.mqtt.ChatMqttToWebSocketHandler;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Setter
@IntegrationComponentScan
@EnableIntegration
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties("mqtt")
public class MqttIntegrationConfig {

    private String brokerUrl;  // MQTT 브로커 URL
    private String clientId;  // MQTT 클라이언트 ID
    private String subscribeTopic;  // MQTT 구독 토픽
    private String publishTopic;  // MQTT 발행 토픽

    /**
     * MQTT 메시지 수신 채널
     * MQTT 구독 어댑터에서 수신한 메시지가 전달되는 채널
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT 메시지 구독 어댑터
     * 지정된 토픽으로부터 MQTT 메시지를 수신하여 mqttInputChannel에 전달
     */
    @Bean
    @Qualifier("inboundAdapter")
    public MessageProducerSupport inboundAdapter() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId, subscribeTopic);
        adapter.setCompletionTimeout(5000);  // 메시지 수신 시간 초과 설정 (5초)
        adapter.setConverter(new DefaultPahoMessageConverter());  // 기본 메시지 변환기 설정
        adapter.setOutputChannel(mqttInputChannel());  // 수신 메시지를 전송할 채널 설정
        return adapter;
    }

    /**
     * MQTT 메시지 발행 핸들러
     * 지정된 MQTT 브로커와 발행 토픽으로 메시지를 송신하는 핸들러
     */
    @Bean
    @Qualifier("outboundAdapter")
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory());
        messageHandler.setAsync(true);  // 비동기 전송 설정
        messageHandler.setDefaultTopic(publishTopic);  // 기본 발행 토픽 설정
        return messageHandler;
    }

    /**
     * MQTT 발행 채널
     * mqttOutbound 메서드에서 사용할 메시지 발행 채널
     */
    @Bean
    @Qualifier("mqttOutboundChannel")
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT 클라이언트 팩토리
     * MQTT 클라이언트의 연결 옵션을 설정하고 관리하는 팩토리
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});  // 연결할 MQTT 브로커 URL 설정
        factory.setConnectionOptions(options);  // 팩토리에 연결 옵션 설정
        return factory;
    }

    /**
     * InboundAdapter로 들어온 데이터를 처리.
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(ChatMqttToWebSocketHandler handler) {
        return (Message<?> message) -> {
            handler.handleMessageFromMqtt((Message<String>) message);
        };
    }

}
