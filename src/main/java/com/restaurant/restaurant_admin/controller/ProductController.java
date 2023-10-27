package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final int pageSize = 2;

    @GetMapping()
    public ModelAndView viewProducts() {
        return new ModelAndView("admin/products/products");
    }

    @GetMapping("/add")
    public ModelAndView viewAddProduct() {
        return new ModelAndView("admin/products/edit-product");
    }

    @GetMapping("/edit-product/{productId}")
    public ModelAndView viewAddProduct(@PathVariable Long productId) {
        return new ModelAndView("admin/products/edit-product");
    }

    @PostMapping("/add")
    public @ResponseBody ResponseEntity<?> createProduct() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/edit-product/{productId}")
    public @ResponseBody ResponseEntity<?> updateProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public @ResponseBody Page<Product> getPageProducts(@RequestParam int page) {
        return productService.getProductPage(page, pageSize);
    }
}
