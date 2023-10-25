package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private Instant dateTimeOfLastLogin;
    private Integer bonuses;
    private Double totalAmount;
    private Integer totalOrders;
    private Boolean isActive;
    @OneToOne(cascade = CascadeType.ALL)
    private UserDetails userDetails;
    @JoinTable(name = "users_product_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JoinColumn(name = "id")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAndDishes> productWishlist;
    @OneToMany(mappedBy = "user",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Address> addresses;
    @JoinTable(name = "shopping_cart",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JoinColumn(name = "id")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAndDishes> shoppingCart;
    @OneToMany(mappedBy = "user")
    private List<Order> userOrders;
}
