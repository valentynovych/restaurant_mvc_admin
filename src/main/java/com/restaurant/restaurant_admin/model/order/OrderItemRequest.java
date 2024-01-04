package com.restaurant.restaurant_admin.model.order;

import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemRequest {

    private Long id;
    private OrderShortResponse order;
    private BigDecimal itemPrice;
    private BigDecimal itemSalePrice;
    private ProductShortResponse product;
    private List<ProductShortResponse> exclusionIngredients;
    private List<ProductShortResponse> additionalIngredients;
    private Boolean isGiftProduct;
    private String promotionName;
    @Enumerated(EnumType.STRING)
    private PromotionCondition promotionCondition;
    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;
    private Integer discountAmount;
    private Integer minimalAmount;
    private String promoCode;
}
