package com.smartshop.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
public class CartRequestDTO {
    private Long productId;
    private Integer qty;
}
