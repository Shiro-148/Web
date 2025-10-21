package com.project.web.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.CartEntity;

public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    Optional<CartEntity> findByAccount(AccountEntity account);
}
