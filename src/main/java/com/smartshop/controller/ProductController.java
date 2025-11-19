package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartshop.model.dto.ProductDTO;
import com.smartshop.response.ApiResponse;
import com.smartshop.service.ProductService;
import com.smartshop.util.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 查詢全部
    @GetMapping({"", "/"})
    public ApiResponse<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category
    ) {
        List<ProductDTO> products = productService.searchProducts(keyword, category);
        return new ApiResponse<>(200, "查詢成功", products);
    }

    // 查單筆
    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long id) {
        return new ApiResponse<>(200, "查詢成功", productService.getProductById(id));
    }

    // 新增產品(限 ADMIN)
    @PostMapping({"", "/"})
    public ApiResponse<ProductDTO> createProduct(@RequestBody ProductDTO productDTO, HttpSession session) {
        SessionUtil.requireAdmin(session);
        return new ApiResponse<>(200, "新增成功", productService.saveProduct(productDTO));
    }

    // 更新產品(限 ADMIN)
    @PutMapping("/{id}")
    public ApiResponse<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO,
            HttpSession session
    ) {
        SessionUtil.requireAdmin(session);
        return new ApiResponse<>(200, "修改成功", productService.updateProduct(id, productDTO));
    }

    // 刪除產品(限 ADMIN)
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id, HttpSession session) {
        SessionUtil.requireAdmin(session);
        productService.deleteProduct(id);
        return new ApiResponse<>(200, "刪除成功", null);
    }
}
