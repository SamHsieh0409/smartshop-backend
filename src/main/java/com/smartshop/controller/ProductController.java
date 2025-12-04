package com.smartshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping
    public ApiResponse<List<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return new ApiResponse<>(
                200,
                "查詢成功",
                productService.searchProducts(keyword, category)
        );
    }

    // 查單筆
    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long id) {
        return new ApiResponse<>(200, "查詢成功",
                productService.getProductById(id));
    }

    // 新增產品（ADMIN）
    @PostMapping
    public ApiResponse<ProductDTO> createProduct(
            @RequestBody ProductDTO dto,
            HttpSession session) {

        SessionUtil.requireAdmin(session);
        return new ApiResponse<>(200, "新增成功",
                productService.saveProduct(dto));
    }

    // 更新（ADMIN）
    @PutMapping("/{id}")
    public ApiResponse<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO dto,
            HttpSession session) {

        SessionUtil.requireAdmin(session);
        return new ApiResponse<>(200, "更新成功",
                productService.updateProduct(id, dto));
    }

    // 刪除（ADMIN）
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(
            @PathVariable Long id,
            HttpSession session) {

        SessionUtil.requireAdmin(session);
        productService.deleteProduct(id);
        return new ApiResponse<>(200, "刪除成功", null);
    }

    // 分頁（純分頁）
    @GetMapping("/page")
    public ApiResponse<Page<ProductDTO>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return new ApiResponse<>(200, "分頁查詢成功",
                productService.getProductsPage(page, size, sortBy, direction));
    }

    // 整合：搜尋 + 分頁 + 排序
    @GetMapping("/filter")
    public ApiResponse<Page<ProductDTO>> filter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {

        return new ApiResponse<>(200, "查詢成功",
                productService.filterProducts(page, size, sortBy, direction, keyword, category));
    }
    
    //取得類別
    @GetMapping("/categories")
    public ApiResponse<List<String>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return new ApiResponse<>(200, "取得分類成功", categories);
    }

}
