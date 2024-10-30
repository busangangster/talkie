package com.e104.realtime.mqtt.dto;

import lombok.Data;

public record MqttMessageSendDto(int userSeq, String content) {
}
