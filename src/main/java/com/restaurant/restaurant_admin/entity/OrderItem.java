package com.restaurant.restaurant_admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JsonIgnore
    private Order order;
    @ManyToOne
    private Product product;
    private BigDecimal itemPrice;
    private BigDecimal itemSalePrice;
    @ManyToMany
    @JoinTable(name = "order_item_exclusion_ingredients",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> exclusionIngredients;
    @ManyToMany
    @JoinTable(name = "order_item_additional_ingredients",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private List<Product> additionalIngredients;
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
