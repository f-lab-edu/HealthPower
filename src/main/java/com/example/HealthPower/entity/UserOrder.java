package com.example.HealthPower.entity;

import jakarta.persistence.*;

@Entity
public class UserOrder {

    @Id
    @GeneratedValue
    private Long order_id;

    @Column
    private OrderStatus status;
}
