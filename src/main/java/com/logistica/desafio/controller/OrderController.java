package com.logistica.desafio.controller;

import com.logistica.desafio.dto.OrderResponseDTO;
import com.logistica.desafio.model.Order;
import com.logistica.desafio.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/process-file")
    public ResponseEntity<String> processFileFromPath(@RequestParam("filename") String filename) {
        try {
            // Chama o serviço para processar o arquivo, passando o nome do arquivo
            orderService.processFileFromPath(filename);
            return ResponseEntity.ok("Arquivo processado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();


        List<OrderResponseDTO> orderResponseDTOs = orders.stream()
                .map(orderService::mapOrderToDTO)
                .collect(Collectors.toList());

        if (orderResponseDTOs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content se não houver pedidos
        }

        return new ResponseEntity<>(orderResponseDTOs, HttpStatus.OK); // 200 OK com a lista de pedidos
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(value -> {
            OrderResponseDTO orderResponseDTO = orderService.mapOrderToDTO(value);
            return new ResponseEntity<>(orderResponseDTO, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
