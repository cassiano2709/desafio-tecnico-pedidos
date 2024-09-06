package com.logistica.desafio.dto;



import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private UserDTO user;
    private LocalDate date;
    private BigDecimal total;
    private List<ProductDTO> products;

}
