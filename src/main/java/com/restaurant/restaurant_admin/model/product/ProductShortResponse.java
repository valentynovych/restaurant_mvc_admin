package com.restaurant.restaurant_admin.model.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductShortResponse {

    private Long productId;
    private String name;
    private String photo;
    private BigDecimal price;
}
