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
@Table(name = "ingredient")
public class Ingredient extends Product {

    @JoinTable(name = "ingredients_main_categories",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "main_category_id"))
    @JoinColumn(name = "id")
    @ManyToMany
    private List<MainCategory> forMainCategory;
    @Enumerated(EnumType.STRING)
    private IngredientCategory ingredientCategory;

}
