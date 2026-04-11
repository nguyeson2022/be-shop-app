package com.project.shopapp.services;

import com.project.shopapp.dtos.ArticleDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Article;
import com.project.shopapp.repositories.ArticleRepository;
import com.project.shopapp.responses.ArticleResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService{

    private final ArticleRepository articleRepository;

    @Override
    @Transactional
    public Article createArticle(ArticleDTO articleDTO) {
        Article newArticle = Article
                .builder()
                .title(articleDTO.getTitle())
                .description(articleDTO.getDescription())
                .thumbnail(articleDTO.getThumbnail())
                .author(articleDTO.getAuthor())
                .build();
        return articleRepository.save(newArticle);
    }

    @Override
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Can not found id: "+ id));
    }

    @Override
    public Page<ArticleResponse> getAllArticle(PageRequest pageRequest) {
        Page<Article> articlePage;
        articlePage = articleRepository.findAll(pageRequest);
        return articlePage.map(ArticleResponse::fromArticle);
    }

    @Override
    @Transactional
    public Article updateArticle(Long articleId, ArticleDTO articleDTO) throws DataNotFoundException {
        LocalDateTime currentTime = LocalDateTime.now();

        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find article with id: " + articleId));

        // Cập nhật thông tin của bài báo từ articleDTO
        existingArticle.setTitle(articleDTO.getTitle());
        existingArticle.setDescription(articleDTO.getDescription());

        existingArticle.setAuthor(articleDTO.getAuthor());
        existingArticle.setUpdatedAt(currentTime);

        if (articleDTO.getThumbnail() != null) {
            existingArticle.setThumbnail(articleDTO.getThumbnail());
        }

        // Lưu thay đổi vào cơ sở dữ liệu
        return articleRepository.save(existingArticle);
    }

    @Transactional
    public void updateArticleThumbnail(Long articleId, String thumbnail) throws Exception{
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            article.setThumbnail(thumbnail);
            articleRepository.save(article);
        } else {
            throw new DataNotFoundException("Article not found with id: " + articleId);
        }
    }


    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}
