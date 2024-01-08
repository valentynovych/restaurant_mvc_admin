package com.restaurant.restaurant_admin.model.staff;

import com.restaurant.restaurant_admin.entity.enums.Role;
import com.restaurant.restaurant_admin.validator.email_validator.StaffEmailExists;
import com.restaurant.restaurant_admin.validator.phone_validator.StaffPhoneExists;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@StaffEmailExists
@StaffPhoneExists
public class StaffRequest {

    private Long staffId;
    @NotNull(message = "Обов'язкове поле")
    private Role staffRole;
    private String password;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 50, message = "Довжина поля не більше 50 символів")
    private String firstName;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 50, message = "Довжина поля не більше 50 символів")
    private String lastName;
    @Email(regexp = "^[a-zA-Z0-9+._-]+@([a-zA-z]{2,10}\\.)+[a-zA-z]{2,5}$", message = "Email не відповідає формату mail@mail.com")
    private String email;
    @NotNull(message = "Обов'язкове поле")
    private Boolean status;
    @Pattern(regexp = "^380(50|66|95|99|67|68|96|97|98|63|93|73)[0-9]{7}", message = "Телефон не відповідає формату")
    private String phone;
    @NotNull(message = "Обов'язкове поле")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    @Size(max = 100, message = "Довжина поля не більше 50 символів")
    private String photoName;
    private MultipartFile photoFile;
}
