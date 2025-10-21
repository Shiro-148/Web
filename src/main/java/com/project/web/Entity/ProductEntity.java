package com.project.web.Entity;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {
    @ManyToMany
    @JoinTable(name = "product_category", joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "idProduct"), inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "idCategory"))
    @JsonIgnore
    private Set<CategoryEntity> categories;

    @ManyToMany
    @JoinTable(name = "product_addon", joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "idProduct"), inverseJoinColumns = @JoinColumn(name = "addon_id", referencedColumnName = "id"))
    @JsonIgnore
    private Set<AddonEntity> addons;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProduct;

    @Column(name = "nameProduct", length = 255, nullable = false)
    private String nameProduct;

    @Column(name = "priceProduct", precision = 10, scale = 3, nullable = false)
    private BigDecimal priceProduct;

    @Column(name = "priceOldProduct", precision = 10, scale = 3)
    private BigDecimal priceOldProduct;

    @Column(name = "favouriteProduct", columnDefinition = "TINYINT(1) DEFAULT 0", nullable = false)
    private Boolean favouriteProduct;

    @Column(name = "imgPathProduct", nullable = false)
    private String imgPathProduct;

    @Column(name = "noteProduct")
    private String noteProduct;

    @Column(name = "original_price", precision = 10, scale = 3, nullable = false)
    private BigDecimal originalPrice;

    // Getters và Setters

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryEntity> categories) {
        this.categories = categories;
    }

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

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Set<AddonEntity> getAddons() {
        return addons;
    }

    public void setAddons(Set<AddonEntity> addons) {
        this.addons = addons;
    }
}
