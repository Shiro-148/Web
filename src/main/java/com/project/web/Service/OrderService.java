package com.project.web.Service;

import java.util.List;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.OrderEntity;

public interface OrderService {
    OrderEntity createOrderFromCart(AccountEntity account);

    List<OrderEntity> listOrdersByAccount(AccountEntity account);
}
