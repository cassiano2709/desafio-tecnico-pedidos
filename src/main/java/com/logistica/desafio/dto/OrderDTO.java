package com.logistica.desafio.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private BigDecimal total;
    private LocalDate date;
    private List<ProductDTO> products;
}