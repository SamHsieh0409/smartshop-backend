package com.smartshop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.CartItemDTO;
import com.smartshop.model.entity.Cart;
import com.smartshop.model.entity.CartItem;
import com.smartshop.model.entity.Product;
import com.smartshop.model.entity.User;
import com.smartshop.repository.CartItemRepository;
import com.smartshop.repository.CartRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public List<CartItemDTO> getCart(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Cart cart = getOrCreateCart(user);

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        return items.stream()
                .map(this::convertToCartItemDTO)
                .toList();
    }

    @Override
    public CartItemDTO addToCart(String username, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("數量必須大於 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("查無商品"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQty(quantity);
        } else {
            item.setQty(item.getQty() + quantity);
        }

        cartItemRepository.save(item);

        return convertToCartItemDTO(item);
    }

    @Override
    public CartItemDTO updateQuantity(String username, Long productId, int quantity) {

        if (quantity <= 0) {
            throw new RuntimeException("數量必須大於 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("商品不在購物車內"));

        item.setQty(quantity);
        cartItemRepository.save(item);

        return convertToCartItemDTO(item);
    }

    @Override
    public void removeItem(String username, Long productId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("商品不在購物車內"));

        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Cart cart = user.getCart();
        if (cart == null) return;

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        cartItemRepository.deleteAll(items);
    }

    // User → Cart，如果沒有就建立一個
    private Cart getOrCreateCart(User user) {

        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);

            user.setCart(cart);
            userRepository.save(user);
        }

        return cart;
    }

    // CartItem -> CartItemDTO
    private CartItemDTO convertToCartItemDTO(CartItem item) {

        CartItemDTO dto = new CartItemDTO();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setPrice(item.getProduct().getPrice());
        dto.setImageUrl(item.getProduct().getImageUrl());
        dto.setQty(item.getQty());

        return dto;
    }
}
