package com.project.shopapp.responses.Comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class PageCommentResponse {
    private Long id;
    private String user;
    private String product;
    private String content;
}
