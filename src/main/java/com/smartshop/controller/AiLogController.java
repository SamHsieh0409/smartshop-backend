package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartshop.model.dto.AiLogCreateRequestDTO;
import com.smartshop.model.dto.AiLogDTO;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.AiLogService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AiLogController {

    @Autowired
    private AiLogService aiLogService;

    // 查詢使用者 AI 紀錄
    @GetMapping("/logs")
    public ApiResponse<List<AiLogDTO>> getLogs(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        List<AiLogDTO> logs = aiLogService.getAiLogHistory(username);
        return new ApiResponse<>(200, "取得 AI 使用紀錄成功", logs);
    }

    // 新增 AI 紀錄
    @PostMapping("/logs")
    public ApiResponse<String> addLog(
            @RequestBody AiLogCreateRequestDTO request,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        aiLogService.addAiLog(username, request.getPrompt(), request.getResponse());
        return new ApiResponse<>(200, "AI 使用紀錄新增成功", null);
    }
}
