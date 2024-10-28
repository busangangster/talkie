package com.e104.realtime.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationListRequest {

    private int userSeq;

    private LocalDateTime day; // yyyy-MM-dd
}
