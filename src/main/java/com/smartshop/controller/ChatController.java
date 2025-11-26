package com.smartshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartshop.model.dto.ChatRequestDTO;
import com.smartshop.model.dto.ChatResponseDTO;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.ChatService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/ai/chat")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ApiResponse<ChatResponseDTO> chat(
            @RequestBody ChatRequestDTO request,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        ChatResponseDTO result = chatService.chat(username, request);
        return new ApiResponse<>(200, "AI 回覆成功", result);
    }
}
