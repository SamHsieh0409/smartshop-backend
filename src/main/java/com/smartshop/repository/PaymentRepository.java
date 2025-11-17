package com.smartshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.Payment;
import com.smartshop.model.entity.User;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
	Optional<Payment> findByOrderId(Long orderId);
}
