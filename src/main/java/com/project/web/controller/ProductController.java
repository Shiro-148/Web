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
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.web.Entity.ProductEntity;
import com.project.web.Service.ProductService;
@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // Hiển thị danh sách sản phẩm
    @GetMapping("/product_list")
    public String getAllProducts(Model model) {
        List<ProductEntity> products = productService.getAllProducts();
        model.addAttribute("products", products); // Truyền danh sách sản phẩm cho Thymeleaf
        return "product_list"; // Tên file HTML (VD: product_list.html)
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


    // @GetMapping("/product_detail/{id}")
    // public String getProductDetail(@PathVariable("id") Long id, Model model) {
    //     ProductEntity product = productService.getProductById(id);
    //     model.addAttribute("product", product); // Truyền sản phẩm cho Thymeleaf
    //     return "product_detail"; // Tên file HTML (VD: product_detail.html)
    // }
}
