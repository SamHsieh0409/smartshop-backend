package com.smartshop.service;

import com.smartshop.model.dto.ChatRequestDTO;
import com.smartshop.model.dto.ChatResponseDTO;

public interface ChatService {

    ChatResponseDTO chat(String username, ChatRequestDTO request);

}
