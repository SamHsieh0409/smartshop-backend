package com.smartshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.OrderItem;
import com.smartshop.model.entity.User;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
	List<OrderItem> findByOrderId(Long orderId);
}
