package com.restaurant.restaurant_admin.model.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class UserRequest {

    private Long userId;
    @NotEmpty(message = "Поле не може бути пустим")
    @Size(max = 50, message = "Може містити більше 50 символів")
    private String firstName;
    @NotEmpty(message = "Поле не може бути пустим")
    @Size(max = 50, message = "Може містити більше 50 символів")
    private String lastName;
    @Pattern(regexp = "\\d{9,15}", message = "Телефон не відповідає формату")
    private String phone;
    @NotNull(message = "Дата не може пуста")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    @NotEmpty(message = "Поле не може бути пустим")
    @Email(message = "Не відповідає формату example@domen.com")
    private String email;

    @NotNull(message = "Не може бути пустим")
    private Boolean isActive;

}
