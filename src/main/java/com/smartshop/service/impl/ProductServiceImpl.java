package com.smartshop.service.impl;

import com.smartshop.model.entity.Product;
import com.smartshop.repository.ProductRepository;
import com.smartshop.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
    private ProductRepository repo;


    @Override
    public List<Product> findAll() {
        return repo.findAll();
    }

    @Override
    public Product findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Product save(Product product) {
        return repo.save(product);
    }

    @Override
    public Product update(Long id, Product newData) {
        return repo.findById(id).map(p -> {
            p.setName(newData.getName());
            p.setPrice(newData.getPrice());
            p.setStock(newData.getStock());
            p.setDescription(newData.getDescription());
            return repo.save(p);
        }).orElse(null);
    }

    @Override
    public boolean deleteById(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
