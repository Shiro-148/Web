package com.project.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.web.Entity.AccountEntity;

public class AccountDTO {
    private Integer id;
    private String email;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private String role;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public static AccountDTO fromEntity(AccountEntity e) {
        if (e == null)
            return null;
        AccountDTO d = new AccountDTO();
        d.setId(e.getId());
        d.setEmail(e.getEmail());
        d.setFullName(e.getFullName());
        d.setBirthDate(e.getBirthDate());
        d.setPhone(e.getPhone());
        d.setRole(e.getRole());
        d.setStatus(e.getStatus());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }
}
