package com.restaurant.restaurant_admin.model;

import lombok.Data;

@Data
public class ProductShortResponse {

    private Long id;
    private String name;
    private Boolean status;
    private Double price;
    private Integer weight;
    private String photo;
}
