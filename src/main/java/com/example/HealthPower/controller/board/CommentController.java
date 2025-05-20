package com.example.HealthPower.controller.board;

import com.example.HealthPower.dto.comment.CommentRequestDTO;
import com.example.HealthPower.dto.comment.CommentResponseDTO;
import com.example.HealthPower.impl.UserDetailsImpl;
import com.example.HealthPower.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

//@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String writeComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @ModelAttribute CommentRequestDTO commentRequestDTO,
                               RedirectAttributes redirectAttributes) {
        commentService.saveComment(userDetails.getUserId(), commentRequestDTO);

        redirectAttributes.addAttribute("productId", commentRequestDTO.getProductId());
        return "redirect:/board/product/" + commentRequestDTO.getProductId();
    }

    //postman용
    /*@PostMapping
    public ResponseEntity writeComment(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @ModelAttribute CommentRequestDTO commentRequestDTO,
                                       RedirectAttributes redirectAttributes) {
        commentService.saveComment(userDetails.getUserId(), commentRequestDTO);

        //다시 상품 상세 페이지로 리다이렉트
        redirectAttributes.addAttribute("productId", commentRequestDTO.getProductId());

        return ResponseEntity.ok("댓글 작성 완료");
    }*/

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable("postId") Long postId) {
        List<CommentResponseDTO> list = commentService.getCommentByProduct(postId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long productId = commentService.deleteComment(commentId, userDetails.getUserId());

        return "redirect:/board/product/" + productId;
    }
}
