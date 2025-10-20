package com.project.web.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.AddressEntity;

public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {
    List<AddressEntity> findByAccountOrderByIsDefaultDescIdDesc(AccountEntity account);

    Optional<AddressEntity> findByIdAndAccount(Integer id, AccountEntity account);

    Optional<AddressEntity> findByAccountAndIsDefaultTrue(AccountEntity account);
}
