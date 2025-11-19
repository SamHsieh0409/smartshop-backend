package com.smartshop.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartshop.model.dto.ProductDTO;
import com.smartshop.model.entity.Product;
import com.smartshop.repository.ProductRepository;
import com.smartshop.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElse(null);
        return (product != null) ? modelMapper.map(product, ProductDTO.class) : null;
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        // DTO → Entity
        Product product = modelMapper.map(productDTO, Product.class);
        
        // save
        product = productRepository.save(product);

        // Entity → DTO
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existing = productRepository.findById(id)
                .orElse(null);

        if (existing == null) return null;

        // 覆蓋可更新欄位
        existing.setName(productDTO.getName());
        existing.setPrice(productDTO.getPrice());
        existing.setStock(productDTO.getStock());
        existing.setDescription(productDTO.getDescription());
        existing.setCategory(productDTO.getCategory());
        existing.setImageUrl(productDTO.getImageUrl());

        existing = productRepository.save(existing);
        return modelMapper.map(existing, ProductDTO.class);
    }

    @Override
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }

	@Override
	public List<ProductDTO> searchProducts(String keyword, String category) {
	

	    List<Product> products;

	    boolean hasKeyword = keyword != null && !keyword.isBlank();
	    boolean hasCategory = category != null && !category.isBlank();

	    if (hasKeyword && hasCategory) {
	        products = productRepository.findByCategoryIgnoreCaseAndNameContainingIgnoreCase(category.trim(), keyword.trim());
	    } else if (hasCategory) {
	        products = productRepository.findByCategoryIgnoreCase(category.trim());
	    } else if (hasKeyword) {
	        products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
	    } else {
	        products = productRepository.findAll();
	    }

	    return products.stream()
	            .map(product -> modelMapper.map(product, ProductDTO.class))
	            .toList();
	}

}

