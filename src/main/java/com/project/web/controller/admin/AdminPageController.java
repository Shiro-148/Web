package com.project.web.Controller.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.web.Entity.CategoryEntity;
import com.project.web.Entity.ProductEntity;
import com.project.web.Service.CategoryService;
import com.project.web.Service.ProductService;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminPageController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping({ "", "/dashboard" })
    public String dashboard() {
        return "admin/admin-home";
    }

    @GetMapping("/products")
    public String products(Model model) {
        List<ProductEntity> products = productService.getAllProductsWithCategories();
        List<CategoryEntity> categories = categoryService.getAllCategories();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalProducts", products.size());
        return "admin/admin-products";
    }

    @GetMapping("/products/dialog")
    public String productDialog() {
        return "admin/admin-dialog-edit-product";
    }

    @GetMapping("/categories")
    public String categories(@RequestParam(value = "categoryId", required = false) Integer categoryId, Model model) {
        List<CategoryEntity> categories = categoryService.getAllCategoriesWithProducts();
        model.addAttribute("categories", categories);

        CategoryEntity selectedCategory = null;
        if (categoryId != null) {
            selectedCategory = categories.stream()
                    .filter(category -> categoryId.equals(category.getIdCategory()))
                    .findFirst()
                    .orElseGet(() -> categoryService.getCategoryWithProducts(categoryId));
        }
        if (selectedCategory == null && !categories.isEmpty()) {
            selectedCategory = categories.get(0);
        }

        List<ProductEntity> selectedCategoryProducts = Collections.emptyList();
        if (selectedCategory != null && selectedCategory.getProducts() != null) {
            selectedCategoryProducts = new ArrayList<>(selectedCategory.getProducts());
        }

        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("selectedCategoryProducts", selectedCategoryProducts);
        return "admin/admin-category";
    }

    @GetMapping("/users")
    public String users() {
        return "admin/admin-client";
    }

    @GetMapping("/orders")
    public String orders() {
        return "admin/admin-order";
    }

    @GetMapping("/settings")
    public String settings() {
        return "admin/admin-setting";
    }
}
