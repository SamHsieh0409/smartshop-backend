package com.smartshop.service;

import com.smartshop.model.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    Product update(Long id, Product newData);
    boolean deleteById(Long id);
}
