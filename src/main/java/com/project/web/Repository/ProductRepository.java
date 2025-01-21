package com.project.web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    
}
