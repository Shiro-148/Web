package com.project.web.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.CartEntity;
import com.project.web.Entity.CartItemEntity;
import com.project.web.Entity.ProductEntity;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Integer> {
    Optional<CartItemEntity> findByCartAndProduct(CartEntity cart, ProductEntity product);

    List<CartItemEntity> findByCart(CartEntity cart);
}
