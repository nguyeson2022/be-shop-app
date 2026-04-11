package com.project.shopapp.services;

import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.*;
import com.project.shopapp.responses.Comment.PageCommentResponse;
import com.project.shopapp.responses.CommentResponse;
import com.project.shopapp.services.Product.ProductService;
import com.project.shopapp.services.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService{
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ProductService productService;
    @Override
    @Transactional
    public Comment insertComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        Product product = productRepository.findById(commentDTO.getProductId()).orElse(null);
        if (user == null || product == null) {
            throw new IllegalArgumentException("User or product not found");
        }
        Comment newComment = Comment.builder()
                .user(user)
                .product(product)
                .content(commentDTO.getContent())
                .build();
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        commentRepository.save(existingComment);
    }

    @Override
    public List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId) {
        List<Comment> comments = commentRepository.findByUserIdAndProductId(userId, productId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByProduct(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

    public Page<PageCommentResponse> findAllByProductId(Long productId, Pageable pageable) {
        Page<Comment> commentPage;
        if (productId == 0) {
            commentPage = commentRepository.findAll(pageable);
        } else {
            commentPage = commentRepository.findAllByProductId(productId, pageable);
        }
        return commentPage.map(comment -> {
            User user = null;
            try {
                user = userService.getUserById(comment.getUser().getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Product product = null;
            try {
                product = productService.getProductById(comment.getProduct().getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return PageCommentResponse.builder()
                    .id(comment.getId())
                    .user(user.getFullName()) // Assuming user has a "name" field
                    .product(product.getName()) // Assuming product has a "name" field
                    .content(comment.getContent())
                    .build();
        });
    }


    public Page<CommentResponse> findAllByUserIdAndProductId(Long userId, Long productId, Pageable pageable) {
        Page<Comment> commentPage = commentRepository.findAllByUserIdAndProductId(userId, productId, pageable);
        return commentPage.map(CommentResponse::fromComment);
    }

    public Comment getCommentById(Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElse(null);
    }
}
