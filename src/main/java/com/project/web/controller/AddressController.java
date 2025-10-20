package com.project.web.Controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
