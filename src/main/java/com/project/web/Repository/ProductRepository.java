package com.project.web.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.web.Entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findByCategories_IdCategory(Integer idCategory);

    @Query("select distinct p from ProductEntity p left join fetch p.categories")
    List<ProductEntity> findAllWithCategories();

    // Truy vấn fetch join để load luôn collection `addons` tránh
    // LazyInitializationException
    @Query("select p from ProductEntity p left join fetch p.addons where p.idProduct = :id")
    Optional<ProductEntity> findByIdWithAddons(@Param("id") Integer id);
}
