package com.example.HealthPower.controller.board;

import com.example.HealthPower.dto.comment.CommentResponseDTO;
import com.example.HealthPower.dto.product.ProductDTO;
import com.example.HealthPower.dto.user.UserDTO;
import com.example.HealthPower.entity.board.Post;
import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.loginUser.LoginUser;
import com.example.HealthPower.repository.ProductRepository;
import com.example.HealthPower.service.CommentService;
import com.example.HealthPower.service.PostService;
import com.example.HealthPower.service.S3Uploader;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//@RestController
@RequestMapping("/board")
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final ProductRepository productRepository;
    private final CommentService commentService;
    private final S3Uploader s3Uploader;

    @Autowired
    private PostService postService;

    /* 로그인 상태 확인 */
    public UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }

        return null;
    }

    @PostMapping("/post")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<List<Post>> getPostsByBoard(@PathVariable Long boardId) {
        List<Post> posts = postService.getPostsByBoard(boardId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /* 상품 게시판 */
    @GetMapping("/product")
    @PreAuthorize("isAuthenticated()")
    public String productList(@RequestParam(defaultValue = "0") int page, Model model) {

        int pageSize = 5;

        Page<Product> productPage = null;

        try {
            productPage = postService.getProductList("상품게시판", page, pageSize);
            System.out.println(">>> 상품 수: " + productPage.getTotalElements());
        } catch (Exception e) {
            System.out.println(">>> 상품 조회 중 에러 발생: " + e.getMessage());
        }

        model.addAttribute("productPage", productPage);

        return "productList";
    }

    @GetMapping("/productCreate")
    @PreAuthorize("isAuthenticated()")
    public String productCreate(ProductDTO productDTO, Model model) {
        model.addAttribute("productDTO", productDTO);
        return "product";
    }

    @PostMapping("/product/post")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createProduct(@LoginUser UserDTO loginUser,
                                                @RequestBody ProductDTO productDTO,
                                                BindingResult bindingResult) {
        // 현재 로그인된 사용자 정보를 가져옴
        UserDetailsImpl currentUser = getCurrentUser();

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("상품 등록 중 잘못된 입력값이 있습니다.");
        }

        postService.createProduct(productDTO, currentUser);

        return ResponseEntity.ok("상품등록 성공");
    }

    @PostMapping("/product/post2")
    @PreAuthorize("isAuthenticated()")
    public String createProduct2(@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                 @LoginUser UserDTO loginUser,
                                 Model model,
                                 BindingResult bindingResult) throws IOException {
        // 현재 로그인된 사용자 정보를 가져옴
        UserDetailsImpl currentUser = getCurrentUser();

        if (loginUser == null) {
            model.addAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/members/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "입력값에 오류가 있습니다.");
            return "/board/product";
        }

        String imageUrl = s3Uploader.uploadFile(productDTO.getPhoto(), "product-image");

        productDTO.setPhotoUrl(imageUrl);

        postService.createProduct(productDTO, currentUser);

        return "redirect:/board/product";
    }

    //상품 상세정보 조회
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        List<CommentResponseDTO> comments = commentService.getCommentByProduct(id);

        model.addAttribute("product", product);
        model.addAttribute("comments", comments);

        return "productDetail";
    }

    //상품 수정
    /*@PutMapping("/product/update/{id}")
    public ResponseEntity updateProduct(@PathVariable("id") Long id,
                                        @LoginUser UserDTO loginUser,
                                        @RequestBody ProductDTO productDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String userId = userDetails.getUserId();

        postService.updateProduct(id, userId, productDTO);
        return ResponseEntity.ok("상품 수정 완료");
    }*/

    @GetMapping("/product/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateProduct(@PathVariable("id") Long id,
                                Model model,
                                @AuthenticationPrincipal UserDetailsImpl principal,
                                RedirectAttributes redirectAttributes) throws java.nio.file.AccessDeniedException {

        if (principal.getUserId() == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/members/login2";
        }

        ProductDTO productDTO = postService.getEditableDTO(id, principal.getUserId());

        model.addAttribute("productDTO", productDTO);
        model.addAttribute("productId", id);

        return "productEdit";
    }

    @PostMapping(value = "/product/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public String updateProduct2(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
                                 @LoginUser UserDTO loginUser,
                                 @RequestParam(value = "photo", required = false) MultipartFile photo,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) throws IOException {

        if (bindingResult.hasErrors()) {
            return "productEdit";
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String userId = userDetails.getUserId();

        postService.updateProduct(id, userId, photo, productDTO);

        redirectAttributes.addFlashAttribute("msg", "상품 수정이 완료되었습니다.");

        return "redirect:/board/product/" + id;
    }

    //상품삭제
    @PostMapping("/product/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProduct(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        UserDetailsImpl currentUser = getCurrentUser();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        if (!product.getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        productRepository.delete(product);

        redirectAttributes.addAttribute("msg", "상품이 정상적으로 삭제되었습니다.");

        return "redirect:/board/product";
    }
}
