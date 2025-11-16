package com.project.web.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.Entity.ProductEntity;
import com.project.web.Repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductEntity> getAllProductsWithCategories() {
        return productRepository.findAllWithCategories();
    }

    @Transactional(readOnly = true)
    public ProductEntity getProductById(Integer id) {
        if (id == null) {
            return null;
        }
        return productRepository.findByIdWithRelations(id)
                .orElseGet(() -> productRepository.findById(id).orElse(null));
    }

    public List<ProductEntity> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategories_IdCategory(categoryId);
    }

    public List<ProductEntity> getProductsByCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return productRepository.findDistinctByCategories_NameCategoryIgnoreCase(categoryName.trim());
    }

    @Transactional
    public ProductEntity saveProduct(ProductEntity product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID sản phẩm không hợp lệ.");
        }

        ProductEntity product = productRepository.findByIdWithRelations(id)
                .orElseGet(() -> productRepository.findById(id).orElse(null));

        if (product == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm để xoá.");
        }

        if (product.getCategories() != null) {
            product.getCategories().clear();
        }
        if (product.getAddons() != null) {
            product.getAddons().clear();
        }

        productRepository.delete(product);
    }
}
