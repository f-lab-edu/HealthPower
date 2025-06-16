package com.example.HealthPower.repository;

import com.example.HealthPower.entity.board.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductName(String productName);

    Page<Product> findAllByBoardName(String boardName, Pageable pageable);
}
