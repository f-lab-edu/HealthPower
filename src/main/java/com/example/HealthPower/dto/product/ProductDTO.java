package com.example.HealthPower.dto.product;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    private String userId;

    @NotBlank(message = "상품 이름은 필수 입력 사항입니다.")
    private String productName;

    //NotBlank는 String 타입에만 사용 가능
    @NotNull(message = "상품 가격은 필수 입력 사항입니다.")
    private Long price;

    @NotBlank(message = "상품 분류는 필수입니다.")
    private String category;

    @NotNull(message = "상품 수량은 필수입니다.")
    @Min(value = 1, message = "상품 수량은 1 이상이어야 합니다.")
    private Long stock;

    private String boardName;

    @NotBlank(message = "내용 입력은 필수입니다.")
    private String content;

    private MultipartFile photo;

    @Column(name = "photo_url")
    private String photoUrl;

}
