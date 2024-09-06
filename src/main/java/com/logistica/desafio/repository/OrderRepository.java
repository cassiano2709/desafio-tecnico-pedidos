package com.logistica.desafio.repository;

import com.logistica.desafio.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
