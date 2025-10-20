package com.project.web.Service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.AddressEntity;
import com.project.web.Repository.AddressRepository;
import com.project.web.Service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repo;

    public AddressServiceImpl(AddressRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<AddressEntity> listByAccount(AccountEntity account) {
        return repo.findByAccountOrderByIsDefaultDescIdDesc(account);
    }

    @Override
    @Transactional
    public AddressEntity create(AddressEntity address, AccountEntity account) {
        address.setAccount(account);
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            repo.findByAccountAndIsDefaultTrue(account).ifPresent(a -> {
                a.setIsDefault(false);
                repo.save(a);
            });
        }
        return repo.save(address);
    }
}
