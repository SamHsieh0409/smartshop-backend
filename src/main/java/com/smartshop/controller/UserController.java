package com.smartshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartshop.model.dto.*;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.UserService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    @Autowired
    private UserService userService;

    // 註冊
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody RegisterRequestDTO req) {
        return new ApiResponse<>(200, "註冊成功",
                userService.register(req));
    }

    // 登入
    @PostMapping("/login")
    public ApiResponse<UserDTO> login(
            @RequestBody LoginRequestDTO req,
            HttpSession session) {

        UserDTO user = userService.login(req);
        SessionUtil.setLoginUser(session, user);

        return new ApiResponse<>(200, "登入成功", user);
    }

    // 登出
    @GetMapping("/logout")
    public ApiResponse<String> logout(HttpSession session) {
        SessionUtil.clearLogin(session);
        return new ApiResponse<>(200, "登出成功", null);
    }

    // 目前登入資料
    @GetMapping("/me")
    public ApiResponse<UserDTO> me(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        return new ApiResponse<>(200, "OK",
                userService.findByUsername(username));
    }

    @GetMapping("/isLoggedIn")
    public ApiResponse<Boolean> isLoggedIn(HttpSession session) {
        boolean loggedIn = SessionUtil.isLoggedIn(session);
        return new ApiResponse<>(200, "查詢登入狀態成功", loggedIn);
    }

}
