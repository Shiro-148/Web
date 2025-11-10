package com.project.web.Service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.CartItemEntity;
import com.project.web.Entity.OrderEntity;
import com.project.web.Entity.OrderItemEntity;
import com.project.web.Repository.OrderItemRepository;
import com.project.web.Repository.OrderRepository;
import com.project.web.Service.CartService;
import com.project.web.Service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
            CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public OrderEntity createOrderFromCart(AccountEntity account) {
        List<CartItemEntity> items = cartService.listItems(account);
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        OrderEntity order = new OrderEntity();
        order.setAccount(account);
        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        order.setStatus("CONFIRMED");
        OrderEntity saved = orderRepository.save(order);

        for (CartItemEntity ci : items) {
            OrderItemEntity oi = new OrderItemEntity();
            oi.setOrder(saved);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getUnitPrice());
            orderItemRepository.save(oi);
        }

        // Clear cart after order creation
        cartService.clearCart(account);
        return saved;
    }

    @Override
    public List<OrderEntity> listOrdersByAccount(AccountEntity account) {
        return orderRepository.findByAccountOrderByCreatedAtDesc(account);
    }

}
