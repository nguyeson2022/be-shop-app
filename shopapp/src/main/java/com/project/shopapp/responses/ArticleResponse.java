package com.project.shopapp.responses;

import com.project.shopapp.models.Article;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleResponse extends BaseResponse {
    private Long id;
    private String author;
    private String title;
    private String description;
    private String thumbnail;
    private String content;

    public static ArticleResponse fromArticle(Article article) {
        ArticleResponse articleResponse = ArticleResponse.builder()
                .id(article.getId())
                .author(article.getAuthor())
                .title(article.getTitle())
                .description(article.getDescription())
                .thumbnail(article.getThumbnail())
                .content(article.getContent())
                .build();
        articleResponse.setCreatedAt(article.getCreatedAt());
        return articleResponse;
    }
}
