package com.example.HealthPower.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class OrderProduct {

    @Id
    @GeneratedValue
    private Long orderProduct_id;
}
