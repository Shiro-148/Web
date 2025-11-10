package com.project.web.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByAccountOrderByCreatedAtDesc(AccountEntity account);
}
