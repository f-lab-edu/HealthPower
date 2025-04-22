package com.example.HealthPower.service;

import com.example.HealthPower.dto.ProductDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.board.Board;
import com.example.HealthPower.entity.board.Post;
import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.PostRepository;
import com.example.HealthPower.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired //하나하나 안해주면 Repository가 null이라고 인식.
    private ProductRepository productRepository;

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getPostsByBoard(Long boardId) {
        return postRepository.findByBoardId(boardId);
    }


    //ProductDTO => 엔티티로 변환
    public Product convertToEntity(ProductDTO productDTO) {
        Product product = new Product();

        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());

        // Board 엔티티의 공통 필드 설정
        product.setContent(productDTO.getContent()); //board 필드
        product.setBoardName(productDTO.getBoardName()); //board 필드
        product.setCreatedAt(LocalDateTime.now()); //테스트로 현재시각 세팅

        return product;
    }

    // 상품 등록 메서드
    public void createProduct(ProductDTO productDTO, UserDetailsImpl currentUser) {
        // 로그인된 사용자 정보가 있을 경우
        if (currentUser != null) {

            // DTO를 엔티티로 변환
            Product product = convertToEntity(productDTO);

            // 상품 저장
            productRepository.save(product);
        }
    }
}
