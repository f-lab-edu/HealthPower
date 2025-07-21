package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class OrderProduct {

    @Id
    @GeneratedValue
    @Column(name = "order_product_id")
    private Long orderProduct_id;
}
