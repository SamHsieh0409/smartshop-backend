package com.smartshop.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class CartDTO {
    private Long id;
    private List<CartItemDTO> items;
    private Double totalPrice;
}
