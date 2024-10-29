package com.e104.realtime.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeeklyConversationRequest {

    private int userSeq;

    private LocalDate startTime;

    private LocalDate endTime;
}
