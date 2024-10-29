package com.e104.realtime.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WeeklyConversationRequest {

    private int userSeq;

    private LocalDate startTime;

    private LocalDate endTime;
}
