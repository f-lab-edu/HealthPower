package com.example.HealthPower.controller;

import com.example.HealthPower.dto.ProductDTO;
import com.example.HealthPower.entity.User;
import com.example.HealthPower.entity.board.Post;
import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
public class BoardController {

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

    @PostMapping("/products/post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createProduct(@RequestBody @Valid ProductDTO productDTO, BindingResult bindingResult) {

        // 현재 로그인된 사용자 정보를 가져옴
        UserDetailsImpl currentUser = getCurrentUser();

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("상품 등록 중 잘못된 입력값이 있습니다.");
        }

        //상품 등록
        postService.createProduct(productDTO, currentUser);

        return ResponseEntity.ok("상품등록 성공");
    }
}
