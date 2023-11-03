package com.restaurant.restaurant_admin.model;

import com.restaurant.restaurant_admin.entity.IngredientCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {

    private Long productId;
    private String name;
    private Boolean isActive;
    private Boolean isIngredient;
    private BigDecimal price;
    private Integer weight;
    private String photo;
    private Boolean isNovelty;
    private String characteristics;
    private String description;
    private Boolean promotionIsActive;
    private PromotionShort promotion;
    private MainCategoryShortResponse mainCategory;
    private SubcategoryDTO subcategory;
    private List<ProductShortResponse> consistsOfIngredients;
    private List<MainCategoryShortResponse> forMainCategory;
    private IngredientCategory ingredientCategory;
}
