package com.smartshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smartshop.model.entity.Product;
import com.smartshop.repository.ProductRepository;

@Component
public class DataInnitializer implements CommandLineRunner {

	@Autowired
	ProductRepository productRepository;
	
	public DataInnitializer(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}
	
	@Override
	public void run(String... args) throws Exception {
		if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setName("SmartPhone A1");
            p1.setPrice(9999.0);
            p1.setStock(20);
            p1.setDescription("最新智慧型手機");

            Product p2 = new Product();
            p2.setName("Laptop X9");
            p2.setPrice(29999.0);
            p2.setStock(10);
            p2.setDescription("高效能筆電");

            productRepository.save(p1);
            productRepository.save(p2);
        }

        System.out.println("✅ ProductRepository 測試成功，目前商品數量：" + productRepository.count());
    }
		

}
