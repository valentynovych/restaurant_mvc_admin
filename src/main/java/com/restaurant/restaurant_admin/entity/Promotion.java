package com.restaurant.restaurant_admin.entity;

import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;
    private Boolean status;
    private String promoCode;
    private String description;
}
