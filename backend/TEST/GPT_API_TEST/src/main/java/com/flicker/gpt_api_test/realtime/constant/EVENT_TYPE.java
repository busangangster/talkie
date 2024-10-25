package com.flicker.gpt_api_test.realtime.constant;

import java.util.Arrays;

public enum EVENT_TYPE {
    RESPONSE__AUDIO__DELTA("response.audio.delta"),
    RESPONSE__OUTPUT_ITEM__DONE("response.output_item.done");

    private final String eventType;

    EVENT_TYPE(String eventType) {
        this.eventType = eventType;
    }

    public static EVENT_TYPE of(String eventType) {
        return Arrays.stream(values()).filter(type -> type.eventType.equals(eventType)).findFirst().orElseThrow();
    }
}
