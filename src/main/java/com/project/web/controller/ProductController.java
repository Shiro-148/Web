package com.project.web.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.project.web.Entity.AddonEntity;
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
    private com.project.web.Service.AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AddonTypeService addonTypeService;

    @GetMapping("/product_list")
    public String getAllProducts(@RequestParam(value = "categoryId", required = false) Integer selectedCategoryId,
            Model model, Principal principal) {
        List<ProductEntity> products;
        if (selectedCategoryId != null) {
            products = productService.getProductsByCategory(selectedCategoryId);
        } else {
            products = productService.getAllProducts();
        }
        List<ProductEntity> availableProducts = products.stream()
                .filter(product -> {
                    Integer stock = product.getStockQuantity();
                    return stock != null && stock > 0;
                })
                .collect(Collectors.toList());
        model.addAttribute("products", availableProducts);

        // favorite ids của user hiện tại (dùng để hiển thị trái tim)
        java.util.Set<Integer> favoriteIds = new java.util.HashSet<>();
        if (principal != null) {
            com.project.web.Entity.AccountEntity account = accountService
                    .getAccountByPhoneWithFavorites(principal.getName());
            if (account != null && account.getFavorites() != null) {
                account.getFavorites().forEach(p -> favoriteIds.add(p.getIdProduct()));
            }
        }
        model.addAttribute("favoriteIds", favoriteIds);

        List<CategoryEntity> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", selectedCategoryId);

        return "product_list";
    }

    @PostMapping("/products/toggle-favourite/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleFavourite(@PathVariable Integer id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        com.project.web.Entity.AccountEntity account = accountService.getAccountByPhone(principal.getName());
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        boolean nowFav = accountService.toggleFavorite(account.getId(), id);
        return ResponseEntity.ok(nowFav);
    }

    @GetMapping("/product_detail/{id}")
    public String getProductDetail(@PathVariable("id") Integer id, Model model, Principal principal) {
        ProductEntity product = productService.getProductById(id);
        if (product.getAddons() == null) {
            product.setAddons(new HashSet<>());
        }
        List<AddonEntity> productAddonsList = new ArrayList<>(product.getAddons());
        model.addAttribute("product", product);
        model.addAttribute("productAddonsList", productAddonsList);

        // Lọc ra các addonType thực sự có addon liên kết với sản phẩm
        List<AddonTypeEntity> addonTypes = addonTypeService.getAllAddonTypes();
        Set<Integer> addonTypeIds = productAddonsList.stream()
                .filter(a -> a.getType() != null)
                .map(a -> a.getType().getId())
                .collect(Collectors.toSet());
        List<AddonTypeEntity> filteredAddonTypes = addonTypes.stream()
                .filter(type -> addonTypeIds.contains(type.getId()))
                .collect(Collectors.toList());
        model.addAttribute("filteredAddonTypes", filteredAddonTypes);

        boolean isFavorite = false;
        if (principal != null) {
            com.project.web.Entity.AccountEntity account = accountService
                    .getAccountByPhoneWithFavorites(principal.getName());
            if (account != null && account.getFavorites() != null) {
                isFavorite = account.getFavorites().stream()
                        .anyMatch(p -> p.getIdProduct().equals(product.getIdProduct()));
            }
        }
        model.addAttribute("isFavorite", isFavorite);

        return "product_detail";
    }
}
