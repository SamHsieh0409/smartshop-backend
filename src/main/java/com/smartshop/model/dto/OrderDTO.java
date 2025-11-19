package com.smartshop.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private List<OrderItemDTO> items;
}
