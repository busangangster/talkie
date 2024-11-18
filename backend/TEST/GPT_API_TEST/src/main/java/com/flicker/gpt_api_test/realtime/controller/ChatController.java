package com.flicker.gpt_api_test.realtime.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flicker.gpt_api_test.realtime.service.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatWebSocketHandler handler;

    @GetMapping("/hello")
    public void hello() throws JsonProcessingException {
        handler.send("'이건 테스트입니다'라고 말해.");
    }
}
