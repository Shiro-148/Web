package com.project.web.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.web.Entity.CategoryEntity;
import com.project.web.Repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategoryEntity> getAllCategoriesWithProducts() {
        return categoryRepository.findAllWithProducts();
    }

    public CategoryEntity getCategoryWithProducts(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdWithProducts(categoryId);
    }

    public CategoryEntity getCategoryById(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId).orElse(null);
    }
}