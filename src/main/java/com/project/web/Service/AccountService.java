package com.project.web.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.web.Entity.AccountEntity;
import com.project.web.Repository.AccountRepository;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    public AccountEntity getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public AccountEntity getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    // Thêm các phương thức tạo, sửa, xóa tài khoản nếu cần
}