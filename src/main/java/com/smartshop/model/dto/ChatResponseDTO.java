package com.smartshop.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {
    private String reply;                 // AI 最終回覆
    private List<ProductDTO> products;    // 推薦商品清單（可為空）
}
