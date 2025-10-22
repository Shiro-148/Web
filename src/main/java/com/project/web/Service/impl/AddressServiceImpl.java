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

    @Override
    public java.util.Optional<AddressEntity> getByIdAndAccount(Integer id, AccountEntity account) {
        return repo.findByIdAndAccount(id, account);
    }

    @Override
    public java.util.Optional<AddressEntity> getDefault(AccountEntity account) {
        return repo.findByAccountAndIsDefaultTrue(account);
    }

    @Override
    @Transactional
    public AddressEntity update(Integer id, AddressEntity updated, AccountEntity account) {
        AddressEntity existing = repo.findByIdAndAccount(id, account)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        if (updated.getReceiverName() != null)
            existing.setReceiverName(updated.getReceiverName());
        if (updated.getPhone() != null)
            existing.setPhone(updated.getPhone());
        if (updated.getStreet() != null)
            existing.setStreet(updated.getStreet());
        if (updated.getWard() != null)
            existing.setWard(updated.getWard());
        if (updated.getDistrict() != null)
            existing.setDistrict(updated.getDistrict());
        if (updated.getCity() != null)
            existing.setCity(updated.getCity());
        if (updated.getPostalCode() != null)
            existing.setPostalCode(updated.getPostalCode());
        if (Boolean.TRUE.equals(updated.getIsDefault())) {
            repo.findByAccountAndIsDefaultTrue(account).ifPresent(a -> {
                if (!a.getId().equals(existing.getId())) {
                    a.setIsDefault(false);
                    repo.save(a);
                }
            });
            existing.setIsDefault(true);
        } else {
            if (updated.getIsDefault() != null) {
                existing.setIsDefault(updated.getIsDefault());
            }
        }
        return repo.save(existing);
    }

    @Override
    @Transactional
    public void delete(Integer id, AccountEntity account) {
        repo.findByIdAndAccount(id, account).ifPresent(a -> {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                throw new RuntimeException("Cannot delete default address");
            }
            repo.delete(a);
        });
    }
}
