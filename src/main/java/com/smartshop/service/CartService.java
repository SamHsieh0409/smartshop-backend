package com.smartshop.service;

import java.util.List;
import com.smartshop.model.dto.CartItemDTO;

public interface CartService {

    // 查詢使用者購物車列表
    List<CartItemDTO> getCartItems(String username);

    // 新增商品到購物車
    CartItemDTO addToCart(String username, Long productId, int quantity);

    // 更新購物車商品數量
    CartItemDTO updateQuantity(String username, Long cartItemId, int quantity);

    // 移除購物車某項目
    void removeItem(String username, Long cartItemId);

    // 清空購物車
    void clearCart(String username);
}
