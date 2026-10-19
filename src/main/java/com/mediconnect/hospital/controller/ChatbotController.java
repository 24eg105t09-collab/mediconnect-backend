package com.mediconnect.hospital.controller;

import com.mediconnect.hospital.dto.ChatRequest;
import com.mediconnect.hospital.dto.ChatResponse;
import com.mediconnect.hospital.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatbotService.processMessage(request));
    }
}
