package com.restaurant.restaurant_admin.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class UserShortDetails {
    private Long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private Date dateOfBirth;
    private String photo;
    private Integer activeBonuses;
    private Integer usedBonuses;
    private Double totalAmount;
    private String email;
    private Boolean isActive;
}
