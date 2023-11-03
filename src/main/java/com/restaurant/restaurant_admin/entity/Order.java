package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant datetimeOfCreate;
    @ManyToOne
    private User user;
    private OrderStatus status;
    private Double totalAmount;
    @ManyToOne
    private Promotion userPromotion;
    private Integer usedBonuses;
    private String payment;
    @ManyToOne
    private Address address;
    private Integer deliveryTime;
    private String reservedTime;
    private String comment;
    @ManyToOne
    private Staff orderPlaced;
    @JoinTable(name = "order_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    @JoinColumn(name = "id")
    @ManyToMany
    private List<Product> products;
}
