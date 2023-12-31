package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, length = 100)
    private String email;
    private Instant dateTimeOfLastLogin;
    private Integer bonuses;
    private Double totalAmount;
    private Integer totalOrders;
    @Column(nullable = false)
    private Boolean isActive;
    @OneToOne(cascade = CascadeType.ALL)
    private UserDetails userDetails;
    @JoinTable(name = "users_product_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JoinColumn(name = "id")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> productWishlist;
    @OneToMany(mappedBy = "user",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Address> addresses;
    @JoinTable(name = "shopping_cart",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    @JoinColumn(name = "id")
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> shoppingCart;
    @OneToMany(mappedBy = "user")
    private List<Order> userOrders;
}
