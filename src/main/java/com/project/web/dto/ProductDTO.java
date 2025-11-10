package com.project.web.dto;

import java.math.BigDecimal;

import com.project.web.Entity.ProductEntity;

public class ProductDTO {
    private Integer idProduct;
    private String nameProduct;
    private String imgPathProduct;
    private BigDecimal priceProduct;
    private BigDecimal priceOldProduct;

    public Integer getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Integer idProduct) {
        this.idProduct = idProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getImgPathProduct() {
        return imgPathProduct;
    }

    public void setImgPathProduct(String imgPathProduct) {
        this.imgPathProduct = imgPathProduct;
    }

    public BigDecimal getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(BigDecimal priceProduct) {
        this.priceProduct = priceProduct;
    }

    public BigDecimal getPriceOldProduct() {
        return priceOldProduct;
    }

    public void setPriceOldProduct(BigDecimal priceOldProduct) {
        this.priceOldProduct = priceOldProduct;
    }

    public static ProductDTO fromEntity(ProductEntity e) {
        if (e == null)
            return null;
        ProductDTO d = new ProductDTO();
        d.setIdProduct(e.getIdProduct());
        d.setNameProduct(e.getNameProduct());
        d.setImgPathProduct(e.getImgPathProduct());
        d.setPriceProduct(e.getPriceProduct());
        d.setPriceOldProduct(e.getPriceOldProduct());
        return d;
    }
}
