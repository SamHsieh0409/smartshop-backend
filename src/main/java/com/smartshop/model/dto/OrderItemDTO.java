package com.smartshop.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Double price;
    private Integer qty;
    private Double subtotal;
}
