package com.restaurant.restaurant_admin.model.promotion;

import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.Instant;

@Data
public class PromotionResponse {

    private Long id;
    private Boolean isActive;
    private String name;
    private String photo;
    @Enumerated(EnumType.STRING)
    private PromotionCondition promotionCondition;
    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;
    private SubcategoryDTO subcategory;
    private MainCategoryDTO forCategory;
    private ProductShortResponse forProduct;
    private ProductShortResponse giftProduct;
    private Integer discountAmount;
    private Integer minimalAmount;
    private String promoCode;
    private Boolean activateOnCode;
    private String description;
    private Instant dateOfCreate;
    private Integer usedCount;
}
