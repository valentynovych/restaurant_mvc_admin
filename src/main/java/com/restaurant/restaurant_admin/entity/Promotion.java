package com.restaurant.restaurant_admin.entity;

import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isActive;
    private String name;
    private String photo;
    @Enumerated(EnumType.STRING)
    private PromotionCondition promotionCondition;
    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;
    @ManyToOne
    private Subcategory subcategory;
    @ManyToOne
    private MainCategory forCategory;
    @ManyToOne
    private Product forProduct;
    @ManyToOne
    private Product giftProduct;
    private Integer discountAmount;
    private Integer minimalAmount;
    private String promoCode;
    private Boolean activateOnCode;
    private String description;
    private Instant dateOfCreate;
    private Integer usedCount;
}
