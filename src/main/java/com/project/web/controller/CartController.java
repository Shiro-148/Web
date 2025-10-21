package com.project.web.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.Entity.AccountEntity;
import com.project.web.Service.AccountService;
import com.project.web.Service.CartService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final AccountService accountService;

    public CartController(CartService cartService, AccountService accountService) {
        this.cartService = cartService;
        this.accountService = accountService;
    }

    private AccountEntity requireAccount(Principal principal) {
        if (principal == null)
            return null;
        return accountService.getAccountByUsername(principal.getName());
    }

    @GetMapping
    public ResponseEntity<?> getCart(Principal principal) {
        AccountEntity account = requireAccount(principal);
        if (account == null)
            return ResponseEntity.status(401).body("Unauthorized");
        Map<String, Object> res = new HashMap<>();
        res.put("cart", cartService.getOrCreateCart(account));
        res.put("items", cartService.listItems(account));
        res.put("total", cartService.calculateTotal(account));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(@RequestBody Map<String, Object> payload, Principal principal) {
        AccountEntity account = requireAccount(principal);
        if (account == null)
            return ResponseEntity.status(401).body("Unauthorized");
        Number productIdNum = (Number) payload.get("productId");
        if (productIdNum == null)
            return ResponseEntity.badRequest().body("productId is required");
        int productId = productIdNum.intValue();
        Number quantityNum = (Number) payload.get("quantity");
        int quantity = quantityNum == null ? 1 : quantityNum.intValue();
        try {
            return ResponseEntity.ok(cartService.addItem(account, productId, quantity));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Integer itemId, @RequestBody Map<String, Object> payload,
            Principal principal) {
        AccountEntity account = requireAccount(principal);
        if (account == null)
            return ResponseEntity.status(401).body("Unauthorized");
        Number quantityNum = (Number) payload.get("quantity");
        Integer quantity = quantityNum == null ? null : quantityNum.intValue();
        try {
            return ResponseEntity.ok(cartService.updateQuantity(account, itemId, quantity));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Integer itemId, Principal principal) {
        AccountEntity account = requireAccount(principal);
        if (account == null)
            return ResponseEntity.status(401).body("Unauthorized");
        try {
            cartService.removeItem(account, itemId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clear(Principal principal) {
        AccountEntity account = requireAccount(principal);
        if (account == null)
            return ResponseEntity.status(401).body("Unauthorized");
        cartService.clearCart(account);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/options")
    public ResponseEntity<?> updateOptions(@RequestBody Map<String, Object> payload, Principal principal) {
        AccountEntity account = requireAccount(principal);
        if (account == null)
            return ResponseEntity.status(401).body("Unauthorized");
        String note = (String) payload.get("note");
        Boolean usePlastic = payload.get("usePlastic") == null ? null : (Boolean) payload.get("usePlastic");
        Boolean useKetchup = payload.get("useKetchup") == null ? null : (Boolean) payload.get("useKetchup");
        Boolean useChillySauce = payload.get("useChillySauce") == null ? null : (Boolean) payload.get("useChillySauce");
        return ResponseEntity.ok(cartService.updateOptions(account, note, usePlastic, useKetchup, useChillySauce));
    }
}
