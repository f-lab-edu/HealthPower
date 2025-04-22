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

    private Long user_id; //이게 공통부분일까??

    private String boardName; //게시판 이름

    private String title; //게시물 제목

    private String content; //게시물 내용

    private LocalDateTime createdAt; //작성일자

    private LocalDateTime updatedAt; //수정일자

    private String photo; //게시물 사진

    private String status; // 게시물 상태
}

