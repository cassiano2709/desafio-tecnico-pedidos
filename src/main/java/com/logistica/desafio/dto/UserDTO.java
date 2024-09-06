package com.logistica.desafio.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private String name;
    private List<OrderDTO> orders;
}