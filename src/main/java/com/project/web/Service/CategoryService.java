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

    public CategoryEntity createCategory(String nameCategory, String imgPathCategory) {
        if (nameCategory == null || nameCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }
        CategoryEntity category = new CategoryEntity();
        category.setNameCategory(nameCategory.trim());
        String resolvedImg = (imgPathCategory != null && !imgPathCategory.trim().isEmpty())
                ? imgPathCategory.trim()
                : "/image/img/menu-list.png";
        category.setImgPath(resolvedImg);
        return categoryRepository.save(category);
    }

    public CategoryEntity updateCategory(Integer idCategory, String nameCategory, String imgPathCategory) {
        if (idCategory == null) {
            throw new IllegalArgumentException("Thiếu ID danh mục.");
        }
        CategoryEntity existing = getCategoryById(idCategory);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục để cập nhật.");
        }
        if (nameCategory == null || nameCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống.");
        }
        existing.setNameCategory(nameCategory.trim());
        if (imgPathCategory != null) {
            String trimmed = imgPathCategory.trim();
            if (!trimmed.isEmpty()) {
                existing.setImgPath(trimmed);
            }
        }
        return categoryRepository.save(existing);
    }

    public void deleteCategory(Integer idCategory) {
        if (idCategory == null) {
            throw new IllegalArgumentException("Thiếu ID danh mục.");
        }
        CategoryEntity existing = getCategoryById(idCategory);
        if (existing == null) {
            throw new IllegalArgumentException("Không tìm thấy danh mục để xoá.");
        }
        if (existing.getProducts() != null) {
            existing.getProducts().forEach(p -> {
                if (p.getCategories() != null) {
                    p.getCategories().remove(existing);
                }
            });
        }
        categoryRepository.delete(existing);
    }
}