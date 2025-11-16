package com.project.web.Controller.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.CategoryEntity;
import com.project.web.Entity.ProductEntity;
import com.project.web.Service.AccountService;
import com.project.web.Service.CategoryService;
import com.project.web.Service.ProductService;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    // DECIMAL(10,3) columns allow max 9,999,999.999 so validate inputs upfront.
    private static final BigDecimal MAX_MONEY = new BigDecimal("9999999.999");
    private static final BigDecimal MIN_MONEY = new BigDecimal("0.000");
    private static final int MONEY_SCALE = 3;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final AccountService accountService;

    public AdminPageController(ProductService productService, CategoryService categoryService,
            AccountService accountService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.accountService = accountService;
    }

    @GetMapping({ "", "/dashboard" })
    public String dashboard() {
        return "admin/admin-home";
    }

    @GetMapping("/products")
    public String products(
            Model model,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "minPrice", required = false) String minPrice,
            @RequestParam(value = "maxPrice", required = false) String maxPrice) {
        List<ProductEntity> products = productService.getAllProductsWithCategories();

        String normalizedKeyword = searchKeyword != null ? searchKeyword.trim().toLowerCase() : null;
        BigDecimal minPriceValue = parseMoneySilently(minPrice);
        BigDecimal maxPriceValue = parseMoneySilently(maxPrice);

        List<ProductEntity> filteredProducts = products.stream()
                .filter(product -> {
                    if (normalizedKeyword == null || normalizedKeyword.isEmpty()) {
                        return true;
                    }
                    String name = product.getNameProduct() != null ? product.getNameProduct().toLowerCase() : "";
                    String note = product.getNoteProduct() != null ? product.getNoteProduct().toLowerCase() : "";
                    return name.contains(normalizedKeyword) || note.contains(normalizedKeyword);
                })
                .filter(product -> {
                    if (categoryId == null) {
                        return true;
                    }
                    if (product.getCategories() == null) {
                        return false;
                    }
                    return product.getCategories().stream()
                            .anyMatch(category -> categoryId.equals(category.getIdCategory()));
                })
                .filter(product -> {
                    if (status == null || status.isEmpty()) {
                        return true;
                    }
                    Integer stock = product.getStockQuantity();
                    int currentStock = stock != null ? stock : 0;
                    if ("in-stock".equals(status)) {
                        return currentStock > 0;
                    } else if ("out-of-stock".equals(status)) {
                        return currentStock <= 0;
                    }
                    return true;
                })
                .filter(product -> {
                    if (minPriceValue == null) {
                        return true;
                    }
                    BigDecimal price = product.getPriceProduct();
                    return price != null && price.compareTo(minPriceValue) >= 0;
                })
                .filter(product -> {
                    if (maxPriceValue == null) {
                        return true;
                    }
                    BigDecimal price = product.getPriceProduct();
                    return price != null && price.compareTo(maxPriceValue) <= 0;
                })
                .collect(Collectors.toList());

        List<CategoryEntity> categories = categoryService.getAllCategories();
        model.addAttribute("products", filteredProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("totalProducts", filteredProducts.size());
        model.addAttribute("filterSearchKeyword", searchKeyword);
        model.addAttribute("filterCategoryId", categoryId);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterMinPrice", minPrice);
        model.addAttribute("filterMaxPrice", maxPrice);
        return "admin/admin-products";
    }

    @GetMapping("/products/dialog")
    public String productDialog() {
        return "admin/admin-dialog-edit-product";
    }

    @PostMapping("/products")
    @Transactional
    public String saveProduct(
            @RequestParam(value = "idProduct", required = false) Integer idProduct,
            @RequestParam("nameProduct") String nameProduct,
            @RequestParam(value = "noteProduct", required = false) String noteProduct,
            @RequestParam(value = "originalPrice", required = false) String originalPrice,
            @RequestParam("priceProduct") String sellingPrice,
            @RequestParam(value = "discountPrice", required = false) String discountPrice,
            @RequestParam(value = "imgPathProduct", required = false) String imgPathProduct,
            @RequestParam(value = "stockQuantity", required = false) Integer stockQuantity,
            @RequestParam(value = "categoryIds", required = false) List<Integer> categoryIds,
            RedirectAttributes redirectAttributes) {
        try {
            ProductEntity product = idProduct != null ? productService.getProductById(idProduct) : new ProductEntity();
            if (product == null) {
                throw new IllegalArgumentException("Không tìm thấy sản phẩm để cập nhật.");
            }

            String trimmedName = nameProduct != null ? nameProduct.trim() : null;
            if (trimmedName == null || trimmedName.isEmpty()) {
                throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
            }
            product.setNameProduct(trimmedName);
            product.setNoteProduct(noteProduct);

            BigDecimal selling = parseMoney(sellingPrice);
            if (selling == null) {
                throw new IllegalArgumentException("Giá bán không hợp lệ.");
            }
            product.setPriceProduct(selling);

            BigDecimal original = parseMoney(originalPrice);
            product.setOriginalPrice(original);

            BigDecimal discount = parseMoney(discountPrice);
            product.setPriceOldProduct(discount != null ? selling.add(discount) : null);

            String resolvedImg = (imgPathProduct != null && !imgPathProduct.trim().isEmpty())
                    ? imgPathProduct.trim()
                    : "/image/img/order-empty.png";
            product.setImgPathProduct(resolvedImg);

            int normalizedStock = (stockQuantity != null && stockQuantity > 0) ? stockQuantity : 0;
            product.setStockQuantity(normalizedStock);

            boolean hasCategorySelection = categoryIds != null && categoryIds.stream().anyMatch(Objects::nonNull);

            if (!hasCategorySelection) {
                Set<CategoryEntity> existingCategories = product.getCategories();
                if (existingCategories == null || existingCategories.isEmpty()) {
                    throw new IllegalArgumentException("Vui lòng chọn ít nhất một danh mục cho sản phẩm.");
                }
            } else {
                List<CategoryEntity> selectedCategories = Objects.requireNonNull(categoryIds).stream()
                        .filter(Objects::nonNull)
                        .map(categoryService::getCategoryById)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (selectedCategories.isEmpty()) {
                    throw new IllegalArgumentException("Danh mục được chọn không hợp lệ.");
                }

                Set<CategoryEntity> categories = product.getCategories();
                if (categories == null) {
                    categories = new HashSet<>();
                    product.setCategories(categories);
                } else {
                    categories.clear();
                }
                categories.addAll(selectedCategories);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu sản phẩm thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu sản phẩm.");
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete")
    @Transactional
    public String deleteProduct(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xoá sản phẩm thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xoá sản phẩm.");
        }
        return "redirect:/admin/products";
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
    public String users(Model model) {
        List<AccountEntity> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalUsers", accounts.size());
        return "admin/admin-client";
    }

    @PostMapping("/users/delete")
    @Transactional
    public String deleteUser(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            accountService.deleteAccountById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xoá người dùng thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xoá người dùng.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/orders")
    public String orders() {
        return "admin/admin-order";
    }

    @GetMapping("/settings")
    public String settings() {
        return "admin/admin-setting";
    }

    private BigDecimal parseMoneySilently(String raw) {
        try {
            return parseMoney(raw);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private BigDecimal parseMoney(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        normalized = normalized.replace(" ", "");
        normalized = normalized.replace(".", "");
        normalized = normalized.replace(",", ".");
        try {
            BigDecimal value = new BigDecimal(normalized);
            value = value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
            if (value.compareTo(MIN_MONEY) < 0 || value.compareTo(MAX_MONEY) > 0) {
                throw new IllegalArgumentException("Giá trị tiền tệ phải nằm trong khoảng 0 đến 9.999.999,999.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Giá trị tiền tệ không hợp lệ: " + raw);
        }
    }
}
