package com.project.shopapp.services.Category;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id);
    List<Category> getAllCategories(int page, int limit);
    Category updateCategory(Long categoryId, CategoryDTO category);
    void deleteCategory(long id);
}
