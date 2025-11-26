package com.smartshop.service;

import java.util.List;
import com.smartshop.model.dto.AiLogDTO;

public interface AiLogService {

    void addAiLog(String username, String prompt, String response);

    List<AiLogDTO> getAiLogHistory(String username);

}
