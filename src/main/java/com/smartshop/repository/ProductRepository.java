package com.smartshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartshop.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByNameContainingIgnoreCase(String keyword);
	List<Product> findByCategoryIgnoreCase(String category);

	List<Product> findByCategoryIgnoreCaseAndNameContainingIgnoreCase(String category, String keyword);

}
