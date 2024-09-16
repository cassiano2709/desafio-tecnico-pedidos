package com.logistica.desafio.service;

import com.logistica.desafio.dto.OrderResponseDTO;
import com.logistica.desafio.dto.ProductDTO;
import com.logistica.desafio.dto.UserDTO;
import com.logistica.desafio.model.Order;
import com.logistica.desafio.model.Product;
import com.logistica.desafio.model.User;
import com.logistica.desafio.repository.OrderRepository;
import com.logistica.desafio.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public OrderResponseDTO mapOrderToDTO(Order order) {
        OrderResponseDTO orderDTO = new OrderResponseDTO();
        orderDTO.setOrderId(order.getId());
        orderDTO.setDate(order.getDate());
        orderDTO.setTotal(order.getTotal());

        User user = order.getUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getId());
        userDTO.setName(user.getName());
        orderDTO.setUser(userDTO);

        List<ProductDTO> products = order.getProducts().stream()
                .map(this::mapProductToDTO)
                .collect(Collectors.toList());
        orderDTO.setProducts(products);

        return orderDTO;
    }

    private ProductDTO mapProductToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getId());
        productDTO.setValue(product.getValue());
        return productDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void processFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IOException("O arquivo está vazio");
        }

        // Definir o tamanho do lote para salvar registros no banco de dados em partes
        int batchSize = 50;
        int count = 0;

        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            Map<Long, User> userMap = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                if (line.length() >= 55) {
                    Long userId = Long.parseLong(line.substring(0, 10).trim());
                    String userName = line.substring(10, 55).trim();
                    Long orderId = Long.parseLong(line.substring(55, 65).trim());
                    Long productId = Long.parseLong(line.substring(65, 75).trim());
                    BigDecimal value = new BigDecimal(line.substring(75, 87).trim());
                    LocalDate date = LocalDate.parse(line.substring(87, 95).trim(), DateTimeFormatter.BASIC_ISO_DATE);

                    User user = userMap.getOrDefault(userId, new User());
                    user.setId(userId);
                    user.setName(userName);

                    Order order = user.getOrders().stream()
                            .filter(o -> o.getId().equals(orderId))
                            .findFirst()
                            .orElse(new Order());

                    order.setId(orderId);
                    order.setDate(date);
                    order.setUser(user);

                    Product product = new Product();
                    product.setId(productId);
                    product.setValue(value);
                    product.setOrder(order);
                    order.getProducts().add(product);

                    BigDecimal total = order.getProducts().stream()
                            .map(Product::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    order.setTotal(total);

                    if (!user.getOrders().contains(order)) {
                        user.getOrders().add(order);
                    }

                    userMap.put(userId, user);
                    count++;

                    // Quando o tamanho do lote é atingido, salva no banco de dados e limpa o contexto
                    if (count % batchSize == 0) {
                        System.out.println("Salvando lote de " + batchSize + " registros...");
                        userRepository.saveAll(userMap.values());
                        entityManager.flush();
                        entityManager.clear();
                        userMap.clear(); // Limpa o mapa para o próximo lote
                    }
                } else {
                    System.out.println("Linha muito curta ou malformada: " + line);
                }
            }

            if (!userMap.isEmpty()) {
                System.out.println("Salvando os registros restantes...");
                userRepository.saveAll(userMap.values());
                entityManager.flush();
                entityManager.clear();
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar o arquivo: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Erro ao processar o arquivo e salvar os dados.", e);
        }
    }
}
