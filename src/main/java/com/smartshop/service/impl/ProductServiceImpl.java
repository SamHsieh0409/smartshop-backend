package com.smartshop.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private static final String DEFAULT_IMAGE_URL = "/images/book.jpg";
    


    @Override
    public List<ProductDTO> getAllProducts() {
    	
        return productRepository.findAll()
                .stream()
                .map(this::convertToProductDTO)
                .toList();
    }


    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("查無商品"));
        return convertToProductDTO(product);
    }


    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
    	if (productDTO.getImageUrl() == null || productDTO.getImageUrl().trim().isEmpty()) {
            productDTO.setImageUrl(DEFAULT_IMAGE_URL);
        }
    	
        Product product = modelMapper.map(productDTO, Product.class);
        return convertToProductDTO(productRepository.save(product));
    }


    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("查無商品"));

        if (productDTO.getImageUrl() == null || productDTO.getImageUrl().trim().isEmpty()) {
            productDTO.setImageUrl(DEFAULT_IMAGE_URL);
        }
        
        modelMapper.map(productDTO, existing);

        return convertToProductDTO(productRepository.save(existing));
    }


    @Override
    public boolean deleteProduct(Long id) {

        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("刪除失敗：商品不存在"));

        productRepository.delete(product);

        return true;
    }


    @Override
    public List<ProductDTO> searchProducts(String keyword, String category) {

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        List<Product> products;

        if (hasKeyword && hasCategory) {
            products = productRepository.findByCategoryIgnoreCaseAndNameContainingIgnoreCase(
                    category.trim(), keyword.trim());
        } else if (hasCategory) {
            products = productRepository.findByCategoryIgnoreCase(category.trim());
        } else if (hasKeyword) {
            products = productRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(this::convertToProductDTO)
                .toList();
    }


    @Override
    public Page<ProductDTO> getProductsPage(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.findAll(pageable)
                .map(this::convertToProductDTO);
    }


    @Override
    public Page<ProductDTO> filterProducts(
            int page, int size, String sortBy, String direction,
            String keyword, String category) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return productRepository.filterProducts(keyword, category, pageable)
                .map(this::convertToProductDTO);
    }
    
    @Override
    public List<String> getAllCategories() {
    	return productRepository.findAll()
    			.stream()
    			.map(p -> p.getCategory())
    			.filter(c -> c != null && !c.isBlank())
    			.distinct()
    			.toList();
    }
    
  //Product → ProductDTO
    private ProductDTO convertToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }


}


