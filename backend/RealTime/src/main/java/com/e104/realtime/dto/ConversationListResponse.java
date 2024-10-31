package com.e104.realtime.dto;

import com.e104.realtime.domain.ConversationAnalytics.ConversationAnalytics;
import com.e104.realtime.domain.User.User;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationListResponse {


    private List<ConversationList> conversationList;

    @Data
    public static class ConversationList {
        private int conversationSeq;
        private String title;
        private LocalDateTime createdAt;
    }

    public ConversationListResponse(User user, LocalDateTime day) {
        List<ConversationAnalytics> conversationAnalytics = user.getConversationAnalytics();
        // Convert the input date to LocalDate to ignore time
        LocalDate targetDate = day.toLocalDate();
        // Find conversations that have the same year, month, and day
        for (ConversationAnalytics conversation : conversationAnalytics) {
            if (conversation.getCreatedAt().toLocalDate().equals(targetDate)) {
                ConversationList conversationList = new ConversationList();
                conversationList.setConversationSeq(conversation.getConversationSeq());
                conversationList.setTitle(conversation.getTitle());
                conversationList.setCreatedAt(conversation.getCreatedAt());
                this.conversationList.add(conversationList);
            }
        }
    }

}
