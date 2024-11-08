package com.e104.realtime.redis.hash;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Builder
@Data
@RedisHash("conversation")
public class Conversation {
    @Id
    private final String id;
    private final long userSeq;
    /**
     * 거짓이면 AI, 참이면 아이
     */
    private final boolean talker;
    private final String content;
    private final LocalDateTime createdAt = LocalDateTime.now();
}
