package com.example.HealthPower.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private BigInteger id;

    @Column(unique = true)
    private String userId;

    @Column(unique = true)
    private String email;

    private String name;

    @Column(unique = true)
    private String nickName;

    private String password;

    private String phoneNumber;

    private String address;

    private String birth;

    private String photo;

    private String role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
