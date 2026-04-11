package com.project.shopapp.services.Category;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        Category newCategory = Category
                .builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {

        Sort sort = Sort.by("id").descending();
        Pageable sortedPageable = PageRequest.of(0, 1000, sort);
        return categoryRepository.findAll(sortedPageable).getContent();
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId,
                                   CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        if (categoryDTO.getThumbnail() != null) {
            existingCategory.setThumbnail(categoryDTO.getThumbnail());
        }
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Transactional
    public void updateCategoryThumbnail(Long categoryId, String thumbnail) throws Exception{
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if(optionalCategory.isPresent()){
            Category category = optionalCategory.get();
            category.setThumbnail(thumbnail);
            categoryRepository.save(category);
        }else{
            throw new DataNotFoundException("Category not found with id: " + categoryId);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }

    public long getTotalCategories() {
        return categoryRepository.count();
    }

}
