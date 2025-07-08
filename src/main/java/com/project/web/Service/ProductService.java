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
        return productRepository.findById(id).orElse(null); // Lấy sản phẩm theo ID
    }

    public ProductEntity updateFavouriteStatus(Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setFavouriteProduct(!product.getFavouriteProduct()); // Đảo trạng thái
                    return productRepository.save(product);
                }).orElse(null);
    }
    public List<ProductEntity> getProductsByCategory(Integer categoryId) {
        return productRepository.findByCategories_IdCategory(categoryId);
    }
}
