package com.restaurant.restaurant_admin.model.order;

import com.restaurant.restaurant_admin.entity.Address;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.model.promotion.PromotionResponse;
import com.restaurant.restaurant_admin.model.staff.StaffResponse;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
public class OrderResponse {
    private Long orderId;
    private Instant datetimeOfCreate;
    private UserShortResponse user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Set<PromotionResponse> usedPromotion;
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
