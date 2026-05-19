package com.example.ChatBoatGeminiApi.controller;

import com.example.ChatBoatGeminiApi.dto.ChatRequest;
import com.example.ChatBoatGeminiApi.service.ChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin("*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {

        return chatService.getResponse(request.getMessage());
    }
}