package com.project.web.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.web.Entity.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity findByPhone(String phone);
    AccountEntity findByEmail(String email);

    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}
