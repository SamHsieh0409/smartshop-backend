package com.smartshop.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter 
@NoArgsConstructor
public class AiLogCreateRequestDTO {
    private String prompt;
    private String response;
}
