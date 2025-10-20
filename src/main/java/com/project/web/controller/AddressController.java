package com.project.web.Controller;

import java.security.Principal;

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
import com.project.web.Entity.AddressEntity;
import com.project.web.Service.AccountService;
import com.project.web.Service.AddressService;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressService addressService;
    private final AccountService accountService;

    public AddressController(AddressService addressService, AccountService accountService) {
        this.addressService = addressService;
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody AddressEntity address, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        AccountEntity account = accountService.getAccountByUsername(principal.getName());
        if (account == null) {
            return ResponseEntity.status(401).body("Account not found");
        }
        AddressEntity saved = addressService.create(address, account);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAddress(@PathVariable Integer id, Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).body("Unauthorized");
        AccountEntity account = accountService.getAccountByUsername(principal.getName());
        return addressService.getByIdAndAccount(id, account)
                .map(a -> ResponseEntity.ok((Object) a))
                .orElse(ResponseEntity.status(404).body((Object) "Not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody AddressEntity updated, Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).body("Unauthorized");
        AccountEntity account = accountService.getAccountByUsername(principal.getName());
        try {
            AddressEntity saved = addressService.update(id, updated, account);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("Not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, Principal principal) {
        if (principal == null)
            return ResponseEntity.status(401).body("Unauthorized");
        AccountEntity account = accountService.getAccountByUsername(principal.getName());
        try {
            addressService.delete(id, account);
            return ResponseEntity.ok().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
