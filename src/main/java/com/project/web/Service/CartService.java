package com.project.web.Service;

import java.math.BigDecimal;
import java.util.List;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.CartEntity;
import com.project.web.Entity.CartItemEntity;

public interface CartService {
    CartEntity getOrCreateCart(AccountEntity account);

    List<CartItemEntity> listItems(AccountEntity account);

    CartItemEntity addItem(AccountEntity account, Integer productId, Integer quantity);

    CartItemEntity updateQuantity(AccountEntity account, Integer itemId, Integer quantity);

    void removeItem(AccountEntity account, Integer itemId);

    void clearCart(AccountEntity account);

    CartEntity updateOptions(AccountEntity account, String note, Boolean usePlastic, Boolean useKetchup,
            Boolean useChillySauce);

    BigDecimal calculateTotal(AccountEntity account);
}
