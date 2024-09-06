package com.logistica.desafio.repository;

import com.logistica.desafio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}