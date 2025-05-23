package com.example.HealthPower.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {

    private Long productId;
    private String content;
}
