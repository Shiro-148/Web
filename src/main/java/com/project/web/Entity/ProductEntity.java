package com.project.web.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduct; // Ánh xạ với INT(11)
    @Column(name="nameProduct", length=255) // Ánh xạ với VARCHAR(200)
    private String nameProduct;
    @Column(name="priceProduct", precision=10, scale=3) // Ánh xạ với VARCHAR(200)
    private BigDecimal priceProduct; // Ánh xạ với DECIMAL(10,3)
    @Column(name="priceOldProduct", precision=10, scale=3)
    private BigDecimal priceOldProduct; // Ánh xạ với DECIMAL(10,3), có thể null
    @Column(name="favouriteProduct", columnDefinition="TINYINT(1)")
    private Boolean favouriteProduct; // Ánh xạ với TINYINT(1)
    @Column(name="imgPathProduct")
    private String imgPathProduct; // Ánh xạ với TEXT
    @Column(name="noteProduct")
    private String noteProduct;

    // Getters và Setters
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

    public Boolean getFavouriteProduct() {
        return favouriteProduct;
    }

    public void setFavouriteProduct(Boolean favouriteProduct) {
        this.favouriteProduct = favouriteProduct;
    }

    public String getImgPathProduct() {
        return imgPathProduct;
    }

    public void setImgPathProduct(String imgPathProduct) {
        this.imgPathProduct = imgPathProduct;
    }

    public String getNoteProduct() {
        return noteProduct;
    }

    public void setNoteProduct(String noteProduct) {
        this.noteProduct = noteProduct;
    }
}
