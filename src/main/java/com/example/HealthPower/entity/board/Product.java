package com.example.HealthPower.entity.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product")
@PrimaryKeyJoinColumn(name = "id") //board에서 id값을 자동으로 받아오려면 이 부분을 꼭 써야함.
public class Product extends Board {

    private String productName;  // 상품 게시판에서 상품 이름
    private Double price;  // 상품 가격
    private String category; //상품 카테고리 (enum 형태로 만들어야할까?)

}
