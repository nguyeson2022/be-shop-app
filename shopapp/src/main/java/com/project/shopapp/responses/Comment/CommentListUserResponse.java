package com.project.shopapp.responses.Comment;

import com.project.shopapp.responses.CommentResponse;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class CommentListUserResponse {
    private List<CommentResponse> comments;
    private int totalPages;
}
