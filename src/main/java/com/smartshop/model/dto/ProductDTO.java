package com.smartshop.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private String imageUrl;      // 商品圖片 URL
    private String description;
    private String category;
    private LocalDateTime createdAt;
}
