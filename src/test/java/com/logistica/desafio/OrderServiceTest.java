package com.logistica.desafio.service;

import com.logistica.desafio.model.Order;
import com.logistica.desafio.model.User;
import com.logistica.desafio.repository.OrderRepository;
import com.logistica.desafio.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @InjectMocks
    private OrderService orderService;

    @Value("${file.upload.directory}")
    private String directory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllOrders() {
        Order order1 = new Order();
        Order order2 = new Order();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        List<Order> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderById() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> foundOrder = orderService.getOrderById(1L);

        assertTrue(foundOrder.isPresent());
        assertEquals(1L, foundOrder.get().getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testProcessFileFromPath() throws Exception {

        String filename = "data.txt";

        Path mockPath = mock(Path.class);
        BufferedReader mockReader = mock(BufferedReader.class);

        when(Files.newBufferedReader(any(Path.class))).thenReturn(mockReader);
        when(mockReader.readLine())
                .thenReturn("1234567890User Name Here       12345678901234567890001234520220101") // Primeira linha
                .thenReturn(null);

        orderService.processFileFromPath(filename);
        verify(userRepository, atLeastOnce()).saveAll(anyList());
    }
}