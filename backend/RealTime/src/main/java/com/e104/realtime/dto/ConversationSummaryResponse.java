package com.e104.realtime.dto;

import com.e104.realtime.domain.vo.ConversationSummary;
import lombok.Data;

@Data
public class ConversationSummaryResponse {
    private String content;

    public ConversationSummaryResponse(ConversationSummary conversationSummary) {
        this.content = conversationSummary.getContent();
    }
}
