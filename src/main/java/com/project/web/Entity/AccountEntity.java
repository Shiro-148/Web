package com.project.web.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 15)
    private String phone; // Số điện thoại — định danh duy nhất

    @Column(nullable = true, unique = true)
    private String email; // Email có thể thêm sau, không bắt buộc khi đăng ký

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true, length = 100)
    private String fullName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "USER";

    @Column(nullable = false)
    private Integer status = 1;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Quan hệ many-to-many tới Product: một account có thể favorite nhiều product
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "account_favorite", joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "idProduct"))
    @JsonIgnore
    private Set<ProductEntity> favorites = new HashSet<>();

    // --- GETTER & SETTER ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<ProductEntity> getFavorites() {
        return favorites;
    }

    public void setFavorites(Set<ProductEntity> favorites) {
        this.favorites = favorites;
    }

    public void addFavorite(ProductEntity product) {
        this.favorites.add(product);
    }

    public void removeFavorite(ProductEntity product) {
        this.favorites.remove(product);
    }

}
