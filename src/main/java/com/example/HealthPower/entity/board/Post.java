package com.example.HealthPower.entity.board;

import com.example.HealthPower.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String author;  // 사용자명 (필요시 User 엔티티와 매핑 가능)

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;  // 작성자(User 테이블과 관계)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters, Setters, Constructors

}
