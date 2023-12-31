package com.restaurant.restaurant_admin.entity;

import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Data
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Boolean isActive;
    @Column(unique = true, nullable = false, length = 100)
    private String name;
    private String photo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionCondition promotionCondition;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionType promotionType;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Subcategory subcategory;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private MainCategory forCategory;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Product forProduct;
    @ManyToOne
    private Product giftProduct;
    private Integer discountAmount;
    private Integer minimalAmount;
    @Column(unique = true, length = 30)
    private String promoCode;
    private Boolean activateOnCode;
    private String description;
    @Column(nullable = false, updatable = false)
    private Instant dateOfCreate;
    @Column(nullable = false)
    private Integer usedCount;
}
