package com.smartshop.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
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
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;


    // 查詢購物車內容
    @Override
    public List<CartItemDTO> getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Cart cart = getOrCreateCart(user);

        return cart.getCartItems()
                .stream()
                .map(item -> modelMapper.map(item, CartItemDTO.class))
                .collect(Collectors.toList());
    }


    // 加入購物車
    @Override
    public CartItemDTO addToCart(String username, Long productId, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 檢查是否已有該商品 → 更新數量
        CartItem existingItem = cart.getCartItems()
                .stream()
                .filter(ci -> ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQty(existingItem.getQty() + quantity);
            cartItemRepository.save(existingItem);
            return modelMapper.map(existingItem, CartItemDTO.class);
        }

        // 建立新項目
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQty(quantity);

        CartItem saved = cartItemRepository.save(newItem);
        return modelMapper.map(saved, CartItemDTO.class);
    }


    // 更新數量
    @Override
    public CartItemDTO updateQuantity(String username, Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("數量必須大於 0");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("該購物車項目不屬於使用者");
        }

        item.setQty(quantity);
        cartItemRepository.save(item);

        return modelMapper.map(item, CartItemDTO.class);
    }


    // 移除購物車單筆資料
    @Override
    public void removeItem(String username, Long cartItemId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("購物車項目不存在"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("無權限刪除此項目");
        }

        cartItemRepository.delete(item);
    }


    // 清空購物車
    @Override
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteAll(cart.getCartItems());
    }


    // =================================
    // Private Helper
    // =================================
    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
}
