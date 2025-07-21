    package com.example.entity.coupon;

    import jakarta.persistence.*;
    import lombok.*;

    import java.time.Instant;
    import java.time.LocalDateTime;

    @Entity
    @Table(name = "coupon")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Coupon {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(nullable = false, length = 50)
        private String name;

        @Column(nullable = false)
        private int amount;

        @Column(name = "total_stock", nullable = false)
        private int totalStock;

        @Column(name = "created_at", nullable = false)
        private Instant createdAt;

        @Column(name = "expired_at")
        private LocalDateTime expiredAt;

    }
