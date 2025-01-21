package com.project.web.Service;
import com.project.web.Repository.ProductRepository;
import com.project.web.Entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll(); // Lấy tất cả sản phẩm từ cơ sở dữ liệu
    }

    public ProductEntity getProductById(Integer id) {
        return productRepository.findById(id).orElse(null); // Lấy sản phẩm theo ID
    }

    // Thêm các phương thức khác nếu cần (VD: thêm, sửa, xóa sản phẩm)
}
