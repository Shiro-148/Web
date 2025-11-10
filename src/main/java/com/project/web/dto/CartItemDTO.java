package com.project.web.dto;

import java.math.BigDecimal;

import com.project.web.Entity.CartItemEntity;

public class CartItemDTO {
    private Integer id;
    private ProductDTO product;
    private BigDecimal unitPrice;
    private Integer quantity;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public static CartItemDTO fromEntity(CartItemEntity e) {
        if (e == null)
            return null;
        CartItemDTO d = new CartItemDTO();
        d.setId(e.getId());
        d.setUnitPrice(e.getUnitPrice());
        d.setQuantity(e.getQuantity());
        d.setProduct(ProductDTO.fromEntity(e.getProduct()));
        return d;
    }
}
