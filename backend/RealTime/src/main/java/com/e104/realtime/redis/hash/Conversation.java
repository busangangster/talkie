package com.e104.realtime.redis.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Conversation {
    private int userSeq;
    /**
     * 거짓이면 AI, 참이면 아이
     */
    private boolean talker;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
}
