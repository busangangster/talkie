package com.e104.realtime.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeeklyConversationRequest {

    private int userSeq;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
