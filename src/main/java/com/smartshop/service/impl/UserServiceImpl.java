package com.smartshop.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.LoginRequestDTO;
import com.smartshop.model.dto.RegisterRequestDTO;
import com.smartshop.model.dto.UserDTO;
import com.smartshop.model.entity.User;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;


    // ================================
    //  註冊 Register
    // ================================
    @Override
    public UserDTO register(RegisterRequestDTO request) {
        // 帳號是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("使用者已存在");
        }

        // 建立 User Entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt 加密
        user.setRole("USER"); // 預設一般使用者

        User saved = userRepository.save(user);

        // 轉成 UserDTO 回傳（不含密碼）
        return modelMapper.map(saved, UserDTO.class);
    }


    // ================================
    //  登入 Login
    // ================================
    @Override
    public UserDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("帳號不存在"));

        // 比對密碼
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        return modelMapper.map(user, UserDTO.class);
    }


    // ================================
    //  查詢使用者（給系統內部用）
    // ================================
    @Override
    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElse(null);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }



}