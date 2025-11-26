package com.smartshop.service;

import java.util.List;
import com.smartshop.model.dto.CartItemDTO;

public interface CartService {


    List<CartItemDTO> getCart(String username);

    CartItemDTO addToCart(String username, Long productId, int quantity);

    CartItemDTO updateQuantity(String username, Long productId, int quantity);

    void removeItem(String username, Long productId);

    void clearCart(String username);

}
