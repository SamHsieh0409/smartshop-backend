package com.smartshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.OrderDTO;
import com.smartshop.model.entity.Cart;
import com.smartshop.model.entity.CartItem;
import com.smartshop.model.entity.Order;
import com.smartshop.model.entity.OrderItem;
import com.smartshop.model.entity.User;
import com.smartshop.repository.CartItemRepository;
import com.smartshop.repository.CartRepository;
import com.smartshop.repository.OrderItemRepository;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ModelMapper modelMapper;


    // 取得使用者訂單
    @Override
    public List<OrderDTO> getOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        List<Order> orders = orderRepository.findByUserId(user.getId());

        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }


    // 查單筆訂單
    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));

        return modelMapper.map(order, OrderDTO.class);
    }


    // 建立訂單（來自購物車）
    @Override
    public OrderDTO createOrder(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("購物車不存在"));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車是空的，無法建立訂單");
        }

        // 建立訂單
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("CREATED");

        Order savedOrder = orderRepository.save(order);

        // 建立訂單項目
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProduct(ci.getProduct());
            oi.setQty(ci.getQty());
            oi.setPrice(ci.getProduct().getPrice()); // 單價存快照

            orderItemRepository.save(oi);
        }

        // 清空購物車
        cartItemRepository.deleteAll(cartItems);

        return modelMapper.map(savedOrder, OrderDTO.class);
    }
}
