package com.project.shopapp.services;

import com.project.shopapp.dtos.ArticleDTO;
import com.project.shopapp.models.Article;
import com.project.shopapp.responses.ArticleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IArticleService {
    Article createArticle(ArticleDTO articleDTO);

    Article getArticleById(Long id);

    Page<ArticleResponse> getAllArticle(PageRequest pageRequest);

    Article updateArticle(Long articleId, ArticleDTO articleDTO) throws Exception;

    void deleteArticle(Long id);
}
