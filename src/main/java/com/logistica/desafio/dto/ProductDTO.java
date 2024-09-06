package com.logistica.desafio.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long productId;
    private BigDecimal value;
}