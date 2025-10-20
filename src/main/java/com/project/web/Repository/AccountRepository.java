package com.project.web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity findByUsername(String username);
    AccountEntity findByEmail(String email);
}