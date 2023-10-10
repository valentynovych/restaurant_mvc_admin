package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product")
public class ProductAndDishes extends Product {

    private Boolean isNovelty;
    private String characteristics;
    private String description;
    private Boolean promotionIsActive;
    // TODO: link to promotion @ManyToOne
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.MERGE})
    private List<Ingredient> consistsOfIngredients;
    @ManyToOne
    private MainCategory mainCategory;
    @ManyToOne
    private Subcategory subcategory;
}
