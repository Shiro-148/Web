package com.project.web.Service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.CartEntity;
import com.project.web.Entity.CartItemEntity;
import com.project.web.Entity.ProductEntity;
import com.project.web.Repository.CartItemRepository;
import com.project.web.Repository.CartRepository;
import com.project.web.Service.CartService;
import com.project.web.Service.ProductService;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
            ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public CartEntity getOrCreateCart(AccountEntity account) {
        return cartRepository.findByAccount(account).orElseGet(() -> {
            CartEntity cart = new CartEntity();
            cart.setAccount(account);
            cart.setUseChillySauce(false);
            cart.setUseKetchup(false);
            cart.setUsePlastic(false);
            return cartRepository.save(cart);
        });
    }

    @Override
    public List<CartItemEntity> listItems(AccountEntity account) {
        CartEntity cart = getOrCreateCart(account);
        return cartItemRepository.findByCart(cart);
    }

    @Override
    @Transactional
    public CartItemEntity addItem(AccountEntity account, Integer productId, Integer quantity) {
        final int qty = (quantity == null || quantity <= 0) ? 1 : quantity;
        CartEntity cart = getOrCreateCart(account);
        ProductEntity product = productService.getProductById(productId);
        if (product == null)
            throw new RuntimeException("Product not found");
        BigDecimal unitPrice = product.getPriceProduct();

        return cartItemRepository.findByCartAndProduct(cart, product)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + qty);
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    CartItemEntity item = new CartItemEntity();
                    item.setCart(cart);
                    item.setProduct(product);
                    item.setUnitPrice(unitPrice);
                    item.setQuantity(qty);
                    return cartItemRepository.save(item);
                });
    }

    @Override
    @Transactional
    public CartItemEntity updateQuantity(AccountEntity account, Integer itemId, Integer quantity) {
        if (quantity == null || quantity <= 0)
            throw new RuntimeException("Quantity must be > 0");
        CartEntity cart = getOrCreateCart(account);
        CartItemEntity item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Forbidden");
        }
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeItem(AccountEntity account, Integer itemId) {
        CartEntity cart = getOrCreateCart(account);
        CartItemEntity item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Forbidden");
        }
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearCart(AccountEntity account) {
        CartEntity cart = getOrCreateCart(account);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartEntity updateOptions(AccountEntity account, String note, Boolean usePlastic, Boolean useKetchup,
            Boolean useChillySauce) {
        CartEntity cart = getOrCreateCart(account);
        if (note != null)
            cart.setNote(note);
        if (usePlastic != null)
            cart.setUsePlastic(usePlastic);
        if (useKetchup != null)
            cart.setUseKetchup(useKetchup);
        if (useChillySauce != null)
            cart.setUseChillySauce(useChillySauce);
        return cartRepository.save(cart);
    }

    @Override
    public BigDecimal calculateTotal(AccountEntity account) {
        CartEntity cart = getOrCreateCart(account);
        return cartItemRepository.findByCart(cart).stream()
                .map(i -> i.getUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
