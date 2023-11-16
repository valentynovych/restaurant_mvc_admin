package com.restaurant.restaurant_admin.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemRequest {

    private Long id;
    private OrderShortResponse order;
    private BigDecimal itemPrice;
    private ProductShortResponse product;
    private List<ProductShortResponse> exclusionIngredients;
    private List<ProductShortResponse> additionalIngredients;
}
