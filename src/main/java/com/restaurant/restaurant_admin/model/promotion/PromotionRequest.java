package com.restaurant.restaurant_admin.model.promotion;

import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PromotionRequest {

    private Long id;
    private Boolean isActive;
    private String name;
    private String photo;
    private PromotionCondition promotionCondition;
    private PromotionType promotionType;
    private SubcategoryDTO subcategory;
    private MainCategoryDTO forCategory;
    private ProductShortResponse giftProduct;
    private ProductShortResponse forProduct;
    private Integer discountAmount;
    private Integer minimalAmount;
    private String promoCode;
    private Boolean activateOnCode;
    private String description;
    private MultipartFile photoFile;
}
