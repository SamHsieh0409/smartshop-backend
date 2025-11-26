package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.smartshop.model.dto.CartItemDTO;
import com.smartshop.model.dto.CartRequestDTO;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.CartService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CartController {

    @Autowired
    private CartService cartService;

    // 取得購物車內容
    @GetMapping
    public ApiResponse<List<CartItemDTO>> getCart(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        List<CartItemDTO> items = cartService.getCart(username);
        return new ApiResponse<>(200, "取得購物車成功", items);
    }

    // 加入購物車（使用 JSON Body）
    @PostMapping("/add")
    public ApiResponse<CartItemDTO> addToCart(
            @RequestBody CartRequestDTO request,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        CartItemDTO item = cartService.addToCart(
                username,
                request.getProductId(),
                request.getQty()
        );
        return new ApiResponse<>(200, "加入成功", item);
    }

    // 更新數量
    @PutMapping("/update")
    public ApiResponse<CartItemDTO> updateQuantity(
            @RequestBody CartRequestDTO request,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        CartItemDTO item = cartService.updateQuantity(
                username,
                request.getProductId(),
                request.getQty()
        );
        return new ApiResponse<>(200, "更新成功", item);
    }

    // 移除購物車中的某商品
    @DeleteMapping("/remove/{productId}")
    public ApiResponse<String> removeItem(
            @PathVariable Long productId,
            HttpSession session) {

        String username = SessionUtil.requireLogin(session);
        cartService.removeItem(username, productId);
        return new ApiResponse<>(200, "刪除成功", null);
    }

    // 清空購物車
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(HttpSession session) {
        String username = SessionUtil.requireLogin(session);
        cartService.clearCart(username);
        return new ApiResponse<>(200, "購物車已清空", null);
    }
}
