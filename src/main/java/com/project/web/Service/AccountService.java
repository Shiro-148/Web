package com.project.web.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.ProductEntity;
import com.project.web.Repository.AccountRepository;
import com.project.web.Repository.ProductRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    // Lấy toàn bộ tài khoản
    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Lấy tài khoản theo số điện thoại (dùng cho login)
    public AccountEntity getAccountByPhone(String phone) {
        return accountRepository.findByPhone(phone);
    }

    // Lấy tài khoản theo số điện thoại và nạp sẵn collection favorites (trong
    // transaction)
    @Transactional(readOnly = true)
    public AccountEntity getAccountByPhoneWithFavorites(String phone) {
        AccountEntity account = accountRepository.findByPhone(phone);
        if (account != null) {
            // Force initialization of lazy collection while session/transaction is open
            account.getFavorites().size();
        }
        return account;
    }

    // Lấy tài khoản theo email (dùng cho phần cập nhật thông tin cá nhân)
    public AccountEntity getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    // Kiểm tra số điện thoại đã tồn tại chưa
    public boolean phoneExists(String phone) {
        return accountRepository.existsByPhone(phone);
    }

    // Kiểm tra email đã tồn tại chưa
    public boolean emailExists(String email) {
        return accountRepository.existsByEmail(email);
    }

    // Tạo tài khoản mới chỉ dùng số điện thoại
    public AccountEntity createAccountByPhone(String phone, String fullName, String password) {
        AccountEntity entity = new AccountEntity();
        entity.setPhone(phone);
        entity.setFullName(fullName);
        // Mã hóa mật khẩu khi tạo tài khoản
        if (password != null) {
            entity.setPassword(passwordEncoder.encode(password));
        } else {
            entity.setPassword(null);
        }
        entity.setRole("USER");
        entity.setStatus(1);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(entity);
    }

    // Update encoded password (used to upgrade plain-text stored passwords)
    public AccountEntity updatePassword(AccountEntity account, String encodedPassword) {
        account.setPassword(encodedPassword);
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    // Cập nhật email sau khi đăng ký
    public AccountEntity updateEmail(AccountEntity account, String newEmail) {
        account.setEmail(newEmail);
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    // Cập nhật thông tin hồ sơ: fullname, email, birthDate
    public AccountEntity updateProfile(AccountEntity account, String newFullName, String newEmail,
            LocalDate birthDate) {
        if (newEmail != null) {
            // nếu email khác email hiện tại và đã tồn tại trong DB -> lỗi
            String current = account.getEmail();
            if (!newEmail.equals(current) && emailExists(newEmail)) {
                throw new IllegalArgumentException("Email đã được sử dụng");
            }
            account.setEmail(newEmail);
        }
        if (newFullName != null) {
            account.setFullName(newFullName);
        }
        account.setBirthDate(birthDate);
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepository.save(account);
    }

    // --- Favorites related methods ---
    public Set<ProductEntity> getFavorites(Integer accountId) {
        AccountEntity account = accountRepository.findById(accountId).orElse(null);
        if (account == null)
            return null;
        // Ensure favorites collection is returned (LAZY loaded when within transaction
        // in controller)
        return account.getFavorites();
    }

    @Transactional
    public boolean toggleFavorite(Integer accountId, Integer productId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // toggle
        if (account.getFavorites().stream().anyMatch(p -> p.getIdProduct().equals(product.getIdProduct()))) {
            account.removeFavorite(product);
            accountRepository.save(account);
            return false;
        } else {
            account.addFavorite(product);
            accountRepository.save(account);
            return true;
        }
    }

    @Transactional
    public void addFavorite(Integer accountId, Integer productId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        account.addFavorite(product);
        accountRepository.save(account);
    }

    @Transactional
    public void removeFavorite(Integer accountId, Integer productId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        account.removeFavorite(product);
        accountRepository.save(account);
    }
}
