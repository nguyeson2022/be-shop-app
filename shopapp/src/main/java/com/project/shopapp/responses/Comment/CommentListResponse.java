package com.project.shopapp.responses.Comment;

import com.project.shopapp.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CommentListResponse {
    private List<PageCommentResponse> comments;
    private int totalPages;
}

