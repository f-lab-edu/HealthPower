package com.example.HealthPower.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private String userId;
    private String content;
    private String nickname;
    private String imageUrl;
    private LocalDateTime createdAt;

}
