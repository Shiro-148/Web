package com.project.web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.OrderItemEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Integer> {

}
