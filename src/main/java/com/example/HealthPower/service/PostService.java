package com.example.HealthPower.service;

import com.example.HealthPower.dto.product.ProductDTO;
import com.example.HealthPower.entity.board.Post;
import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.PostRepository;
import com.example.HealthPower.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Product convertToEntity(ProductDTO productDTO, UserDetailsImpl currentUser) {
        Product product = new Product();

        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());

        // Board 엔티티의 공통 필드 설정
        product.setUserId(currentUser.getUserId());
        product.setContent(productDTO.getContent()); //board 필드
        product.setBoardName(productDTO.getBoardName()); //board 필드
        product.setCreatedAt(LocalDateTime.now()); //테스트로 현재시각 세팅

        return product;
    }

    // 상품 등록 메서드
    //public void createProduct(ProductDTO productDTO, UserDetailsImpl currentUser) {
    public void createProduct(ProductDTO productDTO, UserDetailsImpl currentUser) {
        // 로그인된 사용자 정보가 있을 경우
        if (currentUser != null) {
            Product product = convertToEntity(productDTO, currentUser);

            // 상품 저장
            productRepository.save(product);
        }
    }

    // 상품 수정 메서드
    @Transactional // JPA가 변경된 엔티티를 감지하고 flush해서 DB에 반영.
    public void updateProduct(Long productId, String userId, ProductDTO productDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("작성자만 수정이 가능합니다.");
        }

        product.update(productDTO);
    }
}
