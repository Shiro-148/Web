package com.project.web.Controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.web.Entity.AccountEntity;
import com.project.web.Entity.ProductEntity;
import com.project.web.Service.AccountService;
import com.project.web.Service.ProductService;

@Controller
public class HomeController {

    private static final String BESTSELLER_CATEGORY_NAME = "BESTSELLER";

    @Autowired
    private ProductService productService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        List<ProductEntity> bestsellerProducts = productService
                .getProductsByCategoryName(BESTSELLER_CATEGORY_NAME)
                .stream()
                .filter(product -> {
                    Integer stock = product.getStockQuantity();
                    return stock != null && stock > 0;
                })
                .collect(Collectors.toList());
        model.addAttribute("bestsellerProducts", bestsellerProducts);

        Set<Integer> favoriteIds = new HashSet<>();
        if (principal != null) {
            AccountEntity account = accountService.getAccountByPhoneWithFavorites(principal.getName());
            if (account != null && account.getFavorites() != null) {
                account.getFavorites().forEach(p -> favoriteIds.add(p.getIdProduct()));
            }
        }
        model.addAttribute("favoriteIds", favoriteIds);

        return "index";
    }

    // @GetMapping("/productList")
    // public String getMenuList() {
    // return "product_list";
    // }
    // @GetMapping("/product_detail")
    // public String getProductDetail() {
    // return "product_detail";
    // }
    // @GetMapping("/cart")
    // public String getCart(HttpServletRequest request) {
    // Object username = request.getAttribute("username");
    // if (username == null) {
    // return "redirect:/login";
    // }
    // return "cart";
    // }
}
