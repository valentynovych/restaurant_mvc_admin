package com.restaurant.restaurant_admin.entity;

import com.restaurant.restaurant_admin.entity.enums.IngredientCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    @ManyToOne
    private Promotion promotion;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private MainCategory mainCategory;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Subcategory subcategory;
    @JoinTable(name = "products_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    @ManyToMany
    private List<Product> consistsOfIngredients = new ArrayList<>();
    @JoinTable(name = "ingredients_main_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "main_category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    @ManyToMany
    private Set<MainCategory> forMainCategory = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private IngredientCategory ingredientCategory;
}
