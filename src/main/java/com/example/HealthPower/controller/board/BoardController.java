package com.example.HealthPower.controller.board;

import com.example.HealthPower.dto.ProductDTO;
import com.example.HealthPower.dto.UserDTO;
import com.example.HealthPower.entity.board.Post;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.loginUser.LoginUser;
import com.example.HealthPower.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> createProduct(@LoginUser UserDTO userDTO,
                                                @RequestBody ProductDTO productDTO,
                                                BindingResult bindingResult) {

        // 현재 로그인된 사용자 정보를 가져옴
        UserDetailsImpl currentUser = getCurrentUser();

        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("상품 등록 중 잘못된 입력값이 있습니다.");
        }

        //상품 등록
        //postService.createProduct(productDTO, currentUser);

        postService.createProduct(productDTO, currentUser);

        return ResponseEntity.ok("상품등록 성공");
    }

    @PutMapping("/products/{id}")
    public ResponseEntity updateProduct(@PathVariable("id") Long id,
                                        @LoginUser UserDTO loginUser,
                                        @RequestBody ProductDTO productDTO) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String userId = userDetails.getUserId();

        postService.updateProduct(id, userId, productDTO);
        return ResponseEntity.ok("상품 수정 완료");
    }
}
