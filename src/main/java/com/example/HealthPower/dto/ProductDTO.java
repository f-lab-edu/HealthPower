package com.example.HealthPower.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @NotBlank(message = "상품 이름은 필수 입력 사항입니다.")
    private String productName;

    //NotBlank는 String 타입에만 사용 가능
    @NotNull(message = "상품 가격은 필수 입력 사항입니다.")
    private Double price;

    private String category;

    @NotBlank(message = "등록할 게시판을 선택해야 합니다.")
    private String boardName;

    private String content;


}
