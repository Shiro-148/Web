package com.project.web.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
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
    //     Object username = request.getAttribute("username");
    //     if (username == null) {
    //         return "redirect:/login";
    //     }
    //     return "cart";
    // }
}
