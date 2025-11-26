package com.smartshop.service;

import com.smartshop.model.dto.LoginRequestDTO;
import com.smartshop.model.dto.RegisterRequestDTO;
import com.smartshop.model.dto.UserDTO;
import java.util.List;

public interface UserService {

    UserDTO register(RegisterRequestDTO request);

    UserDTO login(LoginRequestDTO request);

    UserDTO findByUsername(String username);

    boolean existsByUsername(String username);

    void addAiLog(String username, String prompt, String response);

    List<String> getAiLogHistory(String username);

}
