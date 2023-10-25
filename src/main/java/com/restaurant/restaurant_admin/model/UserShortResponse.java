package com.restaurant.restaurant_admin.model;

import lombok.Data;

import java.util.Date;

@Data
public class UserShortResponse {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Date registrationDate;
    private Integer totalOrders;
}
