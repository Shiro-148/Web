package com.project.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.web.Entity.CartEntity;

public class CartDTO {
    private Integer id;
    private String note;
    private Boolean usePlastic;
    private Boolean useKetchup;
    private Boolean useChillySauce;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemDTO> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getUsePlastic() {
        return usePlastic;
    }

    public void setUsePlastic(Boolean usePlastic) {
        this.usePlastic = usePlastic;
    }

    public Boolean getUseKetchup() {
        return useKetchup;
    }

    public void setUseKetchup(Boolean useKetchup) {
        this.useKetchup = useKetchup;
    }

    public Boolean getUseChillySauce() {
        return useChillySauce;
    }

    public void setUseChillySauce(Boolean useChillySauce) {
        this.useChillySauce = useChillySauce;
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

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public static CartDTO fromEntity(CartEntity e) {
        if (e == null)
            return null;
        CartDTO d = new CartDTO();
        d.setId(e.getId());
        d.setNote(e.getNote());
        d.setUsePlastic(e.getUsePlastic());
        d.setUseKetchup(e.getUseKetchup());
        d.setUseChillySauce(e.getUseChillySauce());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        // items will be set by controller (to avoid extra fetch here)
        return d;
    }
}
