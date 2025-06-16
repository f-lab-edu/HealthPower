package com.example.HealthPower.entity.board;

import com.example.HealthPower.dto.product.ProductDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "product")
@PrimaryKeyJoinColumn(name = "id") //board에서 id값을 자동으로 받아오려면 이 부분을 꼭 써야함.
public class Product extends Board {

    private String productName;  // 상품 게시판에서 상품 이름
    private Integer price;  // 상품 가격
    private String category; //상품 카테고리 (enum 형태로 만들어야할까?)
    private int stock; //상품 수량
    private String content; //상품 설명

    public void update(ProductDTO productDTO) {
        this.productName = productDTO.getProductName();
        this.price = productDTO.getPrice();
        this.category = productDTO.getCategory();
        this.stock = productDTO.getStock();
        this.content = productDTO.getContent();
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("재고 부족");
        }
        this.stock -= quantity;
    }

}
