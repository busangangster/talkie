package com.e104.realtime.mqtt.dto.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

public record MqttBaseDto(int userSeq, String header, HashMap<String, String> data) {
}
