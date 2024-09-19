package com.logistica.desafio.service;

import com.logistica.desafio.dto.OrderResponseDTO;
import com.logistica.desafio.model.Order;
import com.logistica.desafio.model.Product;
import com.logistica.desafio.model.User;
import com.logistica.desafio.repository.OrderRepository;
import com.logistica.desafio.repository.UserRepository;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        orderService = new OrderService(userRepository, orderRepository);
        orderService.entityManager = entityManager;
    }

    @Test
    void testGetAllOrders() {

        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));


        List<Order> result = orderService.getAllOrders();


        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderById() {

        Long orderId = 1L;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));


        Optional<Order> result = orderService.getOrderById(orderId);


        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testMapOrderToDTO() {

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Product product1 = new Product();
        product1.setId(100L);
        product1.setValue(new BigDecimal("50.00"));

        Product product2 = new Product();
        product2.setId(101L);
        product2.setValue(new BigDecimal("100.00"));

        Order order = new Order();
        order.setId(1L);
        order.setDate(LocalDate.now());
        order.setUser(user);
        order.setProducts(Arrays.asList(product1, product2));
        order.setTotal(new BigDecimal("150.00"));


        OrderResponseDTO result = orderService.mapOrderToDTO(order);


        assertEquals(1L, result.getOrderId());
        assertEquals(2, result.getProducts().size());
        assertEquals("John Doe", result.getUser().getName());
        assertEquals(new BigDecimal("150.00"), result.getTotal());
    }

    @Test
    void testProcessFileWithSimpleContent() throws Exception {

        // Linha simulada comprimento ajustado para 95 caracteres
        String content = "0000000014                                 Clelia Hills00000001460000000001      673.4920211125    \n";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", inputStream);

        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList(mockUser));


        orderService.processFile(file);

        verify(userRepository, times(1)).saveAll(any());
        verify(entityManager, times(1)).flush();
        verify(entityManager, times(1)).clear();
    }



    @Test
    void testProcessFileEmptyFile() {

        MockMultipartFile file = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        Exception exception = assertThrows(IOException.class, () -> {
            orderService.processFile(file);
        });

        assertEquals("O arquivo está vazio", exception.getMessage());
    }

    @Test
    void testProcessFileWithMalformedLine() throws Exception {

        String malformedContent = "0000000001John Doe                     0000000001\n"; // Linha incompleta
        InputStream inputStream = new ByteArrayInputStream(malformedContent.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", inputStream);


        orderService.processFile(file);


        verify(userRepository, never()).saveAll(any());
    }

    @Test
    void testProcessFileWithBatchSaving() throws Exception {

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            content.append(String.format("%010d%-45s%010d%010d%012d%08d\n", 1L, "John Doe", 1L, 1L, 12345, 20230101));
        }
        InputStream inputStream = new ByteArrayInputStream(content.toString().getBytes(StandardCharsets.UTF_8));
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", inputStream);

        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList(mockUser));


        orderService.processFile(file);


        verify(userRepository, times(2)).saveAll(any()); // Deve salvar em dois lotes
        verify(entityManager, times(2)).flush();
        verify(entityManager, times(2)).clear();
    }
}
