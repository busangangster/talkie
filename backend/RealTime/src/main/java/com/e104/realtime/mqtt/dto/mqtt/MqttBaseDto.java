package com.e104.realtime.mqtt.dto.mqtt;

import java.util.HashMap;

public record MqttBaseDto(int userSeq, String header, HashMap<String, String> data) {
}
