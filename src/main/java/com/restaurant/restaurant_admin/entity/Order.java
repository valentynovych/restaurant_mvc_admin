package com.restaurant.restaurant_admin.entity;

import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant datetimeOfCreate;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Promotion userPromotion;
    private Integer usedBonuses;
    private String payment;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Address address;
    private Integer deliveryTime;
    private String reservedTime;
    private String comment;
    private String cutlery;
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private Staff orderPlaced;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;
}
