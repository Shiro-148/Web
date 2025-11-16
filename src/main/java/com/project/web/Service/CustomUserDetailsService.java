package com.project.web.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.web.Entity.AccountEntity;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        AccountEntity account = accountService.getAccountByPhone(phone);
        if (account == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản với số điện thoại: " + phone);
        }

        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        if (authorities.isEmpty()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        }

        return new User(
                account.getPhone(),
                account.getPassword(),
                authorities);
    }
}
