package com.restaurant.restaurant_admin.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StatisticModel {

    private Integer ordersOfLastMonth;
    private BigDecimal amountOfLastMonth;
    private BigDecimal amountOfLastDay;
    private Integer activeOrders;
}
