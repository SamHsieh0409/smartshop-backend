package com.smartshop.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class CartItemDTO {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String imageUrl;
}

