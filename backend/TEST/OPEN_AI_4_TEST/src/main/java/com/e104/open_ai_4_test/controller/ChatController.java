package com.e104.open_ai_4_test.controller;

import com.e104.open_ai_4_test.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/api/multi-chat")
    public String multiChat() {
        return chatService.testMultiChat();
    }
}
