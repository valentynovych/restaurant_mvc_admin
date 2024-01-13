package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.entity.enums.IngredientCategory;
import com.restaurant.restaurant_admin.model.product.IngredientRequest;
import com.restaurant.restaurant_admin.model.product.ProductRequest;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import com.restaurant.restaurant_admin.service.ProductService;
import com.restaurant.restaurant_admin.utils.UploadFileUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService productService;
    private final UploadFileUtil fileUtil;
    private final int pageSize = 10;

    @GetMapping()
    public ModelAndView viewProducts() {
        return new ModelAndView("admin/products/products");
    }

    @GetMapping("/add")
    public ModelAndView viewAddProduct() {
        return new ModelAndView("admin/products/add-product");
    }

    @GetMapping("/edit-product/{productId}")
    public ModelAndView viewAddProduct(@PathVariable Long productId) {
        return new ModelAndView("admin/products/edit-product");
    }

    @PostMapping("/edit-product/{productId}")
    public @ResponseBody ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                                         @Valid @ModelAttribute ProductRequest product,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public @ResponseBody Page<ProductShortResponse> getPageProducts(@RequestParam int page,
                                                                    @RequestParam(required = false) String search,
                                                                    @RequestParam(required = false) Boolean isIngredient) {

        return productService.getProductsBySearch(page, pageSize, search, isIngredient);
    }

    @GetMapping("/getProduct/{productId}")
    public @ResponseBody ResponseEntity<?> getProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @GetMapping("/getCategories")
    public @ResponseBody ResponseEntity<?> getCategories(@RequestParam int page, @RequestParam String search) {
        return new ResponseEntity<>(productService.getCategories(page, pageSize, search), HttpStatus.OK);
    }

    @GetMapping("/getSubcategories")
    public @ResponseBody ResponseEntity<?> getSubcategories(@RequestParam int page, @RequestParam String search) {
        return new ResponseEntity<>(productService.getSubcategories(page, pageSize, search), HttpStatus.OK);
    }

    @GetMapping("/getIngredients")
    public @ResponseBody ResponseEntity<?> getIngredients(@RequestParam int page, @RequestParam String search) {
        return new ResponseEntity<>(productService.getIngredients(page, pageSize, search), HttpStatus.OK);
    }

    @GetMapping("/getIngredientCategories")
    public @ResponseBody List<IngredientCategory> getIngredientCategories() {
        return Arrays.stream(IngredientCategory.values()).toList();
    }

    @PostMapping("/update-product")
    public @ResponseBody ResponseEntity<?> updateProduct(@Valid @ModelAttribute ProductRequest productRequest,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        productService.updateProduct(productRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/update-ingredient")
    public @ResponseBody ResponseEntity<?> updateIngredient(@Valid @ModelAttribute IngredientRequest ingredientRequest,
                                                            BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        productService.updateIngredient(ingredientRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add-product")
    public @ResponseBody ResponseEntity<?> createProduct(@Valid @ModelAttribute ProductRequest productRequest,
                                                         BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        productService.createProduct(productRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add-ingredient")
    public @ResponseBody ResponseEntity<?> createIngredient(@Valid @ModelAttribute IngredientRequest ingredientRequest,
                                                            @NotNull BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        productService.createProduct(ingredientRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-product/{productId}")
    public @ResponseBody ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        var isDeleted = productService.deleteProductById(productId);

        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-image/{photoName}")
    public @ResponseBody ResponseEntity<?> deleteProduct(@PathVariable String photoName) {
        fileUtil.deleteUploadFile(photoName);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
