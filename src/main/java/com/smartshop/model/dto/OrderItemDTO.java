package com.smartshop.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO {
    private String productName;
    private Double price;
    private Integer quantity;
}
