package com.example.HealthPower.entity.board;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

//게시판 공통 사항
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)  // 상속 관계 설정 (조인 테이블 전략 = 각각의 테이블에 저장)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; //작성자 아이디

    private String boardName; //게시판 이름

    private String title; //게시물 제목

    private String content; //게시물 내용

    private LocalDateTime createdAt; //작성일자

    private LocalDateTime updatedAt; //수정일자

    private String photo; //게시물 사진

    @Column(name = "photo_url")
    private String photoUrl;

    private String status; // 게시물 상태
}

