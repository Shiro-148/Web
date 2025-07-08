package com.project.web.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.web.Entity.AddonTypeEntity;
import com.project.web.Entity.CategoryEntity;
import com.project.web.Entity.ProductEntity;
import com.project.web.Service.AddonTypeService;
import com.project.web.Service.CategoryService;
import com.project.web.Service.ProductService;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AddonTypeService addonTypeService;

    @GetMapping("/product_list")
    public String getAllProducts(@RequestParam(value = "categoryId", required = false) Integer selectedCategoryId,
            Model model) {
        List<ProductEntity> products;
        if (selectedCategoryId != null) {
            products = productService.getProductsByCategory(selectedCategoryId);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);

        List<CategoryEntity> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", selectedCategoryId);

        return "product_list";
    }

    @PostMapping("/products/toggle-favourite/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleFavourite(@PathVariable Integer id) {
        ProductEntity updatedProduct = productService.updateFavouriteStatus(id);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct.getFavouriteProduct());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping("/product_detail/{id}")
    public String getProductDetail(@PathVariable("id") Integer id, Model model) {
        ProductEntity product = productService.getProductById(id);
        model.addAttribute("product", product);

        // Lấy tất cả loại addon (sốt, nước, đồ ăn kèm)
        List<AddonTypeEntity> addonTypes = addonTypeService.getAllAddonTypes();
        model.addAttribute("addonTypes", addonTypes);

        return "product_detail";
    }
}
