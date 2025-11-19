package com.smartshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.PaymentDTO;
import com.smartshop.model.entity.Order;
import com.smartshop.model.entity.Payment;
import com.smartshop.model.entity.User;
import com.smartshop.repository.OrderRepository;
import com.smartshop.repository.PaymentRepository;
import com.smartshop.repository.UserRepository;
import com.smartshop.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;


    // 建立付款紀錄（預設 PENDING）
    @Override
    public PaymentDTO createPayment(String username, Long orderId, String paymentMethod) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrder(order);
        payment.setMethod(paymentMethod);
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        return modelMapper.map(saved, PaymentDTO.class);
    }


    // 模擬付款成功
    @Override
    public PaymentDTO completePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("付款紀錄不存在"));

        payment.setStatus("SUCCESS");
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);
        return modelMapper.map(saved, PaymentDTO.class);
    }


    // 查詢使用者所有付款紀錄
    @Override
    public List<PaymentDTO> getPaymentHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        return paymentRepository.findByUserId(user.getId())
                .stream()
                .map(pay -> modelMapper.map(pay, PaymentDTO.class))
                .collect(Collectors.toList());
    }


    // 查詢單一付款紀錄
    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("付款紀錄不存在"));

        return modelMapper.map(payment, PaymentDTO.class);
    }
}
