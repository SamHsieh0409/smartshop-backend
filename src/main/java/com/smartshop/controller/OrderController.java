package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartshop.model.dto.OrderDTO;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.OrderService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 結帳
    @PostMapping("/checkout")
    public ApiResponse<OrderDTO> checkout(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        OrderDTO order = orderService.checkout(username);
        return new ApiResponse<>(200, "結帳成功", order);
    }

    // 查詢目前登入使用者的所有訂單
    @GetMapping({"", "/"})
    public ApiResponse<List<OrderDTO>> getUserOrders(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        List<OrderDTO> orders = orderService.getUserOrders(username);
        return new ApiResponse<>(200, "取得訂單列表成功", orders);
    }

    // 查詢單一訂單（只能看自己的訂單）
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDTO> getOrderById(
            @PathVariable Long orderId,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        OrderDTO order = orderService.getOrderById(orderId, username);
        return new ApiResponse<>(200, "取得訂單明細成功", order);
    }
}
