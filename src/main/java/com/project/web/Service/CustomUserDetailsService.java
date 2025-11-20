package com.project.web.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
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
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        AccountEntity account = resolveAccount(identifier);
        if (account == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản: " + identifier);
        }

        List<SimpleGrantedAuthority> authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        if (authorities.isEmpty()) {
            authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));
        }

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> "ADMIN".equalsIgnoreCase(a.getAuthority())
                        || "ROLE_ADMIN".equalsIgnoreCase(a.getAuthority()));

        boolean looksLikeAdminLogin = identifier != null
                && identifier.toLowerCase().contains("admin");

        if (!isAdmin && !looksLikeAdminLogin && !isPhoneOrEmail(identifier)) {
            throw new BadCredentialsException("Người dùng chỉ được đăng nhập bằng SĐT hoặc Email");
        }

        return new User(
                account.getPhone(),
                account.getPassword(),
                authorities);
    }

    private AccountEntity resolveAccount(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return null;
        }

        String value = identifier.trim();

        if (isPhone(value)) {
            return accountService.getAccountByPhone(value);
        }

        if (isEmail(value)) {
            return accountService.getAccountByEmail(value);
        }

        return accountService.getAccountByPhone(value);
    }

    private boolean isPhone(String value) {
        return value != null && value.matches("^\\d{9,11}$");
    }

    private boolean isEmail(String value) {
        return value != null && value.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private boolean isPhoneOrEmail(String value) {
        return isPhone(value) || isEmail(value);
    }
}
