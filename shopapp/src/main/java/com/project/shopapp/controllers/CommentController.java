package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.responses.Comment.CommentListResponse;
import com.project.shopapp.responses.Comment.CommentListUserResponse;
import com.project.shopapp.responses.Comment.PageCommentResponse;
import com.project.shopapp.responses.CommentResponse;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("${api.prefix}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentRepository commentRepository;

    @GetMapping("")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam("product_id") Long productId
    ) {
        List<CommentResponse> commentResponses;
        if (userId == null) {
            commentResponses = commentService.getCommentsByProduct(productId);
        } else {
            commentResponses = commentService.getCommentsByUserAndProduct(userId, productId);
        }
        return ResponseEntity.ok(commentResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateComment(
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) throws Exception {
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean isAdmin = loginUser.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        boolean isCommentOwner = loginUser.getId().equals(commentDTO.getUserId());

        if (isAdmin || isCommentOwner) {
            commentService.updateComment(commentId, commentDTO);
            return ResponseEntity.ok(new ResponseObject("Update comment successfully", HttpStatus.OK, null));
        } else {
            return ResponseEntity.badRequest().body(new ResponseObject("You do not have permission to update this comment", HttpStatus.BAD_REQUEST, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment != null) {
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(comment -> {
                    commentRepository.delete(comment);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> insertComment(
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        // Insert the new comment
        User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(loginUser.getId() != commentDTO.getUserId()) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot comment as another user",
                            HttpStatus.BAD_REQUEST,
                            null));
        }
        commentService.insertComment(commentDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Insert comment successfully")
                        .status(HttpStatus.OK)
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<CommentListResponse> getAllComments(
            @RequestParam("product_id") Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws Exception {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page - 1, limit,
                Sort.by("id").ascending()
        );
        Page<PageCommentResponse> commentPage = commentService.findAllByProductId(productId, pageRequest);

        // Lấy tổng số trang
        int totalPages = commentPage.getTotalPages();
        List<PageCommentResponse> comments = commentPage.getContent();
        return ResponseEntity.ok().body(CommentListResponse
                .builder()
                .comments(comments)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseObject> getAllCommentsByUserIdAndProductId(
            @RequestParam("user_id") Long userId,
            @RequestParam("product_id") Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws Exception {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page - 1, limit,
                Sort.by("id").ascending()
        );
        Page<CommentResponse> commentPage = commentService.findAllByUserIdAndProductId(userId, productId, pageRequest);

        // Lấy tổng số trang
        int totalPages = commentPage.getTotalPages();
        List<CommentResponse> comments = commentPage.getContent();
        CommentListUserResponse commentListUserResponse = CommentListUserResponse
                .builder()
                .comments(comments)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get comments user list successfully")
                .status(HttpStatus.OK)
                .data(commentListUserResponse)
                .build());
    }


}

