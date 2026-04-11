package com.project.shopapp.repositories;

import com.project.shopapp.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByUserIdAndProductId(@Param("userId") Long userId,
                                           @Param("productId") Long productId);
    List<Comment> findByProductId(@Param("productId") Long productId);

    Page<Comment> findAllByProductId(Long productId, Pageable pageable);

    Page<Comment> findAllByUserIdAndProductId(Long userId, Long productId, Pageable pageable);

}
