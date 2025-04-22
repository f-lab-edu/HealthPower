package com.example.HealthPower.repository;

import com.example.HealthPower.entity.board.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
