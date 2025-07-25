package com.example.service;

import com.example.dto.comment.CommentRequestDTO;
import com.example.dto.comment.CommentResponseDTO;
import com.example.entity.User;
import com.example.entity.board.Comment;
import com.example.entity.board.Product;
import com.example.repository.CommentRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Comment saveComment(String userId, CommentRequestDTO commentRequestDTO) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않음"));

        Product product = productRepository.findById(commentRequestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("게시물이 존재하지 않음"));

        Comment comment = Comment.builder()
                .user(user)
                .product(product)
                .content(commentRequestDTO.getContent())
                .build();

        return commentRepository.save(comment);
    }

    public List<CommentResponseDTO> getCommentByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream()
                .map(comment -> CommentResponseDTO.builder()
                        .commentId(comment.getId())
                        .userId(comment.getUser().getUserId())
                        .content(comment.getContent())
                        .nickname(comment.getUser().getNickname())
                        .imageUrl(comment.getUser().getImageUrl())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long deleteComment(Long commentId, String userId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        Long productId = comment.getProduct().getId();

        commentRepository.delete(comment);

        return productId;
    }
}
