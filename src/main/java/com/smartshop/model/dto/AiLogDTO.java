package com.smartshop.model.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class AiLogDTO {
    private Long id;
    private String prompt;
    private String response;
    private LocalDateTime createdAt;
}

