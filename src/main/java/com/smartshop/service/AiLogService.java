package com.smartshop.service;

import java.util.List;

import com.smartshop.model.dto.AiLogDTO;

public interface AiLogService {

    // 新增一筆 AI 使用紀錄
    void addAiLog(String username, String prompt, String response);

    // 取得指定使用者的 AI 使用紀錄
    List<AiLogDTO> getAiLogHistory(String username);
}
