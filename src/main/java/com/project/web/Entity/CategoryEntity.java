package com.project.web.Entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategory")
    private Integer idCategory;

    @Column(name = "nameCategory", length = 255, nullable = false)
    private String nameCategory;

    @Column(name = "imgPathCategory", length = 255, nullable = false)
    private String imgPathCategory;

    @ManyToMany(mappedBy = "categories")
    private Set<ProductEntity> products;

    public Integer getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Integer idCategory) {
        this.idCategory = idCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public Set<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(Set<ProductEntity> products) {
        this.products = products;
    }

    public String getImgPath() {
        return imgPathCategory;
    }

    public void setImgPath(String imgPath) {
        this.imgPathCategory = imgPath;
    }
}
