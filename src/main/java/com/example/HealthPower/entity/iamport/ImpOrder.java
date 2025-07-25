package com.example.HealthPower.entity.iamport;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ImpOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String merchantUid;

    private String orderName;

    private int quantity;

    private int amount;

    private String status; // PENDING, PAID, FAILED 등

    private LocalDateTime createdAt;

}
