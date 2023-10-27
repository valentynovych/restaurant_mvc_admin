package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.model.ProductShortResponse;
import com.restaurant.restaurant_admin.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductService {

    private final ProductRepo productRepo;
    //protected final ProductMapper mapper;

    public Page<Product> getProductPage(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("name"));
        List<Product> products = productRepo.findAllProductsAndIngredients();
        List<ProductShortResponse> responseList;
        return new PageImpl<>(products, pageable, products.size());
    }

}
