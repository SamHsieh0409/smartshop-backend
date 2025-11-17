package com.smartshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.CartItem;
import com.smartshop.model.entity.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
	List<CartItem> findByCartId(Long cartId);
}
