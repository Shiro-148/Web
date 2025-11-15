package com.project.web.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.web.Entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    @Query("select distinct c from CategoryEntity c left join fetch c.products")
    List<CategoryEntity> findAllWithProducts();

    @Query("select c from CategoryEntity c left join fetch c.products where c.idCategory = :categoryId")
    CategoryEntity findByIdWithProducts(Integer categoryId);
}