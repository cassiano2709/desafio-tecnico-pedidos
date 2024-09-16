package com.logistica.desafio.controller;

import com.logistica.desafio.dto.OrderResponseDTO;
import com.logistica.desafio.model.Order;
import com.logistica.desafio.service.OrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint para upload do arquivo
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ApiOperation(value = "Upload de arquivo de pedidos", notes = "Permite fazer o upload de um arquivo para processar pedidos.")
    public ResponseEntity<String> uploadFile(
            @ApiParam(value = "Arquivo de pedidos para upload", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            // Chama o serviço para processar o arquivo
            orderService.processFile(file);
            return ResponseEntity.ok("Arquivo processado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    // Endpoint para obter todos os pedidos
    @GetMapping
    @ApiOperation(value = "Obter todos os pedidos", notes = "Retorna a lista de todos os pedidos.")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponseDTO> orderResponseDTOs = orders.stream()
                .map(orderService::mapOrderToDTO)
                .collect(Collectors.toList());

        if (orderResponseDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(orderResponseDTOs, HttpStatus.OK);
    }

    // Endpoint para obter um pedido pelo ID
    @GetMapping("/{orderId}")
    @ApiOperation(value = "Obter pedido por ID", notes = "Retorna um pedido pelo seu ID.")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(value -> {
            OrderResponseDTO orderResponseDTO = orderService.mapOrderToDTO(value);
            return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
