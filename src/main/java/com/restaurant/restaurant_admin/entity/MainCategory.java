package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "main_category")
public class MainCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String categoryName;
    private Boolean status;
    private String previewIcon;
    private Integer topPosition;
    private Date dateOfCreate;
    private Integer countChildProduct;
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL)
    private List<Subcategory> subcategories;
    @OneToMany(mappedBy = "mainCategory")
    private List<ProductAndDishes> childProducts;
}
