package com.example.HealthPower.service;

import com.example.HealthPower.dto.product.ProductDTO;
import com.example.HealthPower.entity.board.Post;
import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.repository.PostRepository;
import com.example.HealthPower.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ProductRepository productRepository;
    private final S3Uploader s3Uploader;
    public Post createPost(Post post) {
        return postRepository.save(post);
    }
    public List<Post> getPostsByBoard(Long boardId) {
        return postRepository.findByBoardId(boardId);
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //ProductDTO => 엔티티로 변환
    public Product convertToEntity(ProductDTO productDTO, UserDetailsImpl currentUser) {
        Product product = new Product();

        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setImageUrl(productDTO.getImageUrl());

        // Board 엔티티의 공통 필드 설정
        product.setUserId(currentUser.getUserId());
        product.setContent(productDTO.getContent()); //board 필드
        product.setBoardName("상품게시판"); //board 필드
        product.setCreatedAt(LocalDateTime.now()); //테스트로 현재시각 세팅
        product.setStock(productDTO.getStock());

        return product;
    }

    // 상품 등록 메서드
    @Transactional
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
    public void updateProduct(Long productId, String userId, ProductDTO productDTO) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품이 존재하지 않습니다."));

        if (!product.getUserId().equals(userId)) {
            throw new RuntimeException("작성자만 수정이 가능합니다.");
        }

        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setStock(productDTO.getStock());
        product.setContent(productDTO.getContent());

        if (productDTO.getImageUrl() != null && !productDTO.getImageUrl().isEmpty()) {
            String url = productDTO.getImageUrl();
            product.setImageUrl(url);
        }
    }

    public Page<Product> getProductList(String boardName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findAllByBoardName(boardName, pageable);
    }

    @Transactional(readOnly = true)
    public ProductDTO getEditableDTO(Long id, String requesterId) throws AccessDeniedException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        /* 수정 권한 체크 */
        if (!product.getUserId().equals(requesterId)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        /* --- 엔티티 → DTO 수동 매핑 --- */
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setProductName(product.getProductName());
        productDTO.setPrice(product.getPrice());
        productDTO.setStock(product.getStock());
        productDTO.setCategory(product.getCategory());
        productDTO.setContent(product.getContent());
        productDTO.setImageUrl(product.getImageUrl());   // 이미 저장된 이미지 URL

        return productDTO;
    }
}

