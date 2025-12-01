package com.smartshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.OrderDTO;
import com.smartshop.model.dto.OrderItemDTO;
import com.smartshop.model.dto.PaymentDTO;
import com.smartshop.model.entity.Cart;
import com.smartshop.model.entity.CartItem;
import com.smartshop.model.entity.Order;
import com.smartshop.model.entity.OrderItem;
import com.smartshop.model.entity.Product;
import com.smartshop.model.entity.User;
import com.smartshop.repository.CartItemRepository;
import com.smartshop.repository.OrderItemRepository;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.repository.ProductRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public OrderDTO checkout(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Cart cart = user.getCart();
        if (cart == null) {
            throw new RuntimeException("購物車不存在");
        }

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車為空");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order = orderRepository.save(order);

        double total = 0;

        for (CartItem ci : cartItems) {

            Product product = ci.getProduct();

            if (product.getStock() < ci.getQty()) {
                throw new RuntimeException("商品庫存不足：" + product.getName());
            }

            product.setStock(product.getStock() - ci.getQty());
            productRepository.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQty(ci.getQty());
            oi.setPrice(product.getPrice());

            orderItemRepository.save(oi);

            total += oi.getPrice() * oi.getQty();
        }

        order.setTotalAmount(total);
        order.setStatus("PENDING");
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return convertToOrderDTO(order);
    }

    @Override
    public List<OrderDTO> getUserOrders(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToOrderDTO)
                .toList();
    }

    @Override
    public OrderDTO getOrderById(Long orderId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("查無使用者"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("沒有權限查看此訂單");
        }

        return convertToOrderDTO(order);
    }

    // Order -> OrderDTO
    private OrderDTO convertToOrderDTO(Order order) {

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        List<OrderItemDTO> itemDTOs = items.stream().map(oi -> {
            OrderItemDTO i = new OrderItemDTO();
            i.setProductId(oi.getProduct().getId());
            i.setProductName(oi.getProduct().getName());
            i.setPrice(oi.getPrice());
            i.setQty(oi.getQty());
            i.setSubtotal(oi.getPrice() * oi.getQty());
            return i;
        }).toList();

        dto.setItems(itemDTOs);

        paymentRepository.findByOrderId(order.getId())
        .ifPresent(payment ->
                dto.setPayment(modelMapper.map(payment, PaymentDTO.class))
        );        
        
        return dto;
    }
}
