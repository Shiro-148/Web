package com.project.web.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findByCategories_IdCategory(Integer idCategory);
}
