package com.smartshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartshop.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByNameContainingIgnoreCase(String keyword);

	List<Product> findByCategoryIgnoreCase(String category);

	List<Product> findByCategoryIgnoreCaseAndNameContainingIgnoreCase(String category, String keyword);

	//搜尋 + 類別篩選 + 分頁 + 排序 API
	@Query("""
			   SELECT p FROM Product p
			   WHERE (:keyword IS NULL OR p.name LIKE %:keyword%)
			   AND (:category IS NULL OR p.category = :category)
			""")
	Page<Product> filterProducts(@Param("keyword") String keyword, @Param("category") String category,
			Pageable pageable);

}
