package com.restaurant.restaurant_admin.model;

import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OrderCriteria {

    private Instant dateFrom;
    private Instant dateTo;
    private OrderStatus orderStatus;
    private String search;
}
