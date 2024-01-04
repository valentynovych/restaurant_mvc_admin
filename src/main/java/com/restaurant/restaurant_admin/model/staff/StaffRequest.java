package com.restaurant.restaurant_admin.model.staff;

import com.restaurant.restaurant_admin.entity.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
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
    @Email(regexp = "^(.+)@(\\S+)$", message = "Email не відповідає формату mail@mail.com")
    private String email;
    @NotNull(message = "Обов'язкове поле")
    private Boolean status;
    @Pattern(regexp = "^\\d{9,15}", message = "Телефон не відповідає формату")
    private String phone;
    //    @NotNull(message = "Обов'язкове поле")
    //@NotNull(message = "Обов'язкове поле")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    @Size(max = 100, message = "Довжина поля не більше 50 символів")
    private String photoName;
    private MultipartFile photoFile;
}
