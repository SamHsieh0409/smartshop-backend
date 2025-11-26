package com.smartshop.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.smartshop.model.dto.ProductDTO;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    boolean deleteProduct(Long id);
	List<ProductDTO> searchProducts(String keyword, String category);
	Page<ProductDTO> getProductsPage(int page, int size, String sortBy, String direction);

	Page<ProductDTO> filterProducts(int page, int size, String sortBy, String direction, String keyword, String category);

}
