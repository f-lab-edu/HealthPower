package com.example.entity;

import com.example.enumpackage.OrderStatus;
import jakarta.persistence.*;

@Entity
public class UserOrder {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long order_id;

    @Column
    private OrderStatus status;
}
