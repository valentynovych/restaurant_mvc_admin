package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "subcategory")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subcategoryName;
    @ManyToOne
    private MainCategory parentCategory;
    @OneToMany(mappedBy = "subcategory", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST})
    private List<Product> products;
    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH})
    private List<Promotion> promotions;
}
