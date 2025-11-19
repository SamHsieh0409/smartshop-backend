package com.smartshop.service;

import java.util.List;
import com.smartshop.model.dto.ProductDTO;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    boolean deleteProduct(Long id);
	List<ProductDTO> searchProducts(String keyword, String category);
}
