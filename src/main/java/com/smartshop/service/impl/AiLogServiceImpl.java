package com.smartshop.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.AiLogDTO;
import com.smartshop.model.entity.AiLog;
import com.smartshop.model.entity.User;
import com.smartshop.repository.AiLogRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.AiLogService;

@Service
public class AiLogServiceImpl implements AiLogService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiLogRepository aiLogRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void addAiLog(String username, String prompt, String response) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        AiLog log = new AiLog();
        log.setUser(user);
        log.setPrompt(prompt);
        log.setResponse(response);

        aiLogRepository.save(log);
    }

    @Override
    public List<AiLogDTO> getAiLogHistory(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        return aiLogRepository.findByUserId(user.getId())
                .stream()
                .map(log -> modelMapper.map(log, AiLogDTO.class))
                .toList();
    }
}
