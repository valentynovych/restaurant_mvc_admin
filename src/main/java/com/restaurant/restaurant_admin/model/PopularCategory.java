package com.restaurant.restaurant_admin.model;

import lombok.Data;

@Data
public class PopularCategory {
    private String categoryName;
    private Long countUsed;

    public PopularCategory(String categoryName, Long countUsed) {
        this.categoryName = categoryName;
        this.countUsed = countUsed;
    }
}
