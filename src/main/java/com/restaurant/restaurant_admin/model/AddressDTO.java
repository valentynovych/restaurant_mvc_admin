package com.restaurant.restaurant_admin.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String addressName;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String city;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String street;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String building;
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String apartment;
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String entrance;
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String doorCode;
    @Size(max = 50, message = "Довжина не більше 50 символів")
    private String floor;
    @Size(max = 150, message = "Довжина не більше 150 символів")
    private String comment;
    private UserShortResponse user;
}
