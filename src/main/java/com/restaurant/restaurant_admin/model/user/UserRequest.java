package com.restaurant.restaurant_admin.model.user;

import com.restaurant.restaurant_admin.validator.email_validator.UserEmailExists;
import com.restaurant.restaurant_admin.validator.phone_validator.UserPhoneExists;
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
    @Pattern(regexp = "^380(50|66|95|99|67|68|96|97|98|63|93|73)[0-9]{7}", message = "Телефон не відповідає формату")
    @UserPhoneExists
    private String phone;
    @NotNull(message = "Обов'язкове поле")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    @NotEmpty(message = "Поле не може бути пустим")
    @Email(regexp = "^[a-zA-Z0-9+._-]+@([a-zA-z]{2,10}\\.)+[a-zA-z]{2,5}$" ,message = "Не відповідає формату example@domen.com")
    @UserEmailExists
    private String email;
    @NotNull(message = "Не може бути пустим")
    private Boolean isActive;

}
