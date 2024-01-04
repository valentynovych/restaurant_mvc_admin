package com.restaurant.restaurant_admin.model.staff;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaffLoginRequest {

    @NotEmpty(message = "Обов'язкове поле")
    @Email(message = "Пошта не відповідає формату")
    private String username;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(min = 4, message = "Пароль має бути більше 4 символів")
    private String password;
}
