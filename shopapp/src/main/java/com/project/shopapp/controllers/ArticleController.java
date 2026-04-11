package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ArticleDTO;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Article;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.responses.*;
import com.project.shopapp.services.ArticleService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final LocaleResolver localeResolver;
    private final MessageSource messageSource;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<Article> createArticle(
            @Valid @RequestBody ArticleDTO articleDTO,
            BindingResult result) {
        ArticleResponse articleResponse = new ArticleResponse();
        Article article = new Article();
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(article);
        }
        article = articleService.createArticle(articleDTO);
        return ResponseEntity.ok(article);
    }

    @GetMapping("")
    public ResponseEntity<ArticleListResponse> getAllArticles(
            @RequestParam(value = "page", defaultValue = "0")     int page,
            @RequestParam("limit")    int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by("id").ascending());

        Page<ArticleResponse> articlePage = articleService.getAllArticle(pageRequest);
        int totalPages = articlePage.getTotalPages();
        List<ArticleResponse> articles = articlePage.getContent();
        return ResponseEntity.ok(ArticleListResponse
                .builder()
                .articles(articles)
                .totalPages(totalPages)
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleDTO articleDTO
    )throws DataNotFoundException {
        articleService.updateArticle(id, articleDTO);
        return ResponseEntity.ok("UPDATE_ARTICLE_SUCCESSFULLY");
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
            articleService.deleteArticle(id);
            return ResponseEntity.ok("Delete article successfully");

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(
            @PathVariable("id") Long articleId
    ) {
        try {
            Article existingArticle = articleService.getArticleById(articleId);
            return ResponseEntity.ok(ArticleResponse.fromArticle(existingArticle));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadThumbnail(
            @PathVariable("id") Long articleId,
            @RequestParam("files") MultipartFile file
    ) {
        try {


            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload.");
            }

            // Kiểm tra kích thước file và định dạng
            if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("File size is too large. Maximum size allowed is 10MB.");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Unsupported file format. Please upload an image file.");
            }

            // Lưu file và cập nhật thumbnail trong đối tượng Article
            String filename = storeFile(file);
            ArticleDTO articleDTO = new ArticleDTO();
            articleDTO.setThumbnail(filename);

            articleService.updateArticleThumbnail(articleId, articleDTO.getThumbnail());

            return ResponseEntity.ok().body("Thumbnail uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

}
