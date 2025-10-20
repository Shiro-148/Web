package com.project.web.Service;

import java.util.List;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.AddressEntity;

public interface AddressService {
    List<AddressEntity> listByAccount(AccountEntity account);

    AddressEntity create(AddressEntity address, AccountEntity account);

    java.util.Optional<AddressEntity> getByIdAndAccount(Integer id, AccountEntity account);

    AddressEntity update(Integer id, AddressEntity updated, AccountEntity account);

    void delete(Integer id, AccountEntity account);
}
