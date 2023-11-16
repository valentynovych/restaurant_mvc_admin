package com.restaurant.restaurant_admin.model;

import com.restaurant.restaurant_admin.entity.Address;
import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Instant datetimeOfCreate;
    private UserShortResponse user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Promotion userPromotion;
    private Integer usedBonuses;
    private String payment;
    private Address address;
    private Integer deliveryTime;
    private String reservedTime;
    private String comment;
    private String cutlery;
    private StaffResponse orderPlaced;
    private List<OrderItemResponse> orderItems;
}
