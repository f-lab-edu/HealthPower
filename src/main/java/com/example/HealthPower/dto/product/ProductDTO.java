package com.example.HealthPower.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @NotBlank(message = "상품 이름은 필수 입력 사항입니다.")
    private String productName;

    //NotBlank는 String 타입에만 사용 가능
    @NotNull(message = "상품 가격은 필수 입력 사항입니다.")
    private Integer price;

    @NotBlank(message = "상품 분류는 필수입니다.")
    private String category;

    @NotNull(message = "상품 수량은 필수입니다.")
    @Min(value = 1, message = "상품 수량은 1 이상이어야 합니다.")
    private Integer stock;

    @NotBlank(message = "등록할 게시판을 선택해야 합니다.")
    private String boardName;

    @NotBlank(message = "내용 입력은 필수입니다.")
    private String content;

    private MultipartFile photo;

}
