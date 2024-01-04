package com.restaurant.restaurant_admin.model.order;

import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderShortResponse {

    private Long orderId;
    private Instant datetimeOfCreate;
    private UserShortResponse user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> orderItems;
}
