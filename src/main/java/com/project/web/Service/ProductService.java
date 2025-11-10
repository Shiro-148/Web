package com.project.web.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.web.Entity.ProductEntity;
import com.project.web.Repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public ProductEntity getProductById(Integer id) {
        // Sử dụng query có fetch join để load luôn collection `addons` và tránh
        // LazyInitializationException
        return productRepository.findByIdWithAddons(id).orElseGet(() -> productRepository.findById(id).orElse(null));
    }

    public List<ProductEntity> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategories_IdCategory(categoryId);
    }
}
