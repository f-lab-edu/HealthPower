package com.example.entity.payment;

import com.example.enumpackage.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private double amount;

    private String productName;

    private int quantity;

    @Column(name = "balance_after", nullable = false)
    private Long balanceAfter;

    private LocalDateTime createdAt = LocalDateTime.now();

    public TransactionHistory(String userId, TransactionType type, Long amount, Long balanceAfter, String productName, int quantity) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.productName = productName;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
    }
}
