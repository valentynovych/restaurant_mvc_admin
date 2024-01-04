package com.restaurant.restaurant_admin.model.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class UserShortResponse {

    private Long userId;
    @Size(max = 100, message = "Не довше 100 символів")
    @Pattern(regexp = "^[a-zA-Z0-9+._-]+@([a-zA-z]{2,10}\\.)+[a-zA-z]{2,5}$",
            message = "Пошта не відповідає фомату example@com.ua")
    private String email;
    @Size(max = 50, message = "Не довше 50 символів")
    private String firstName;
    @Size(max = 50, message = "Не довше 50 символів")
    private String lastName;
    @NotEmpty(message = "Обов'язкове поле")
    @Pattern(regexp = "^380(50|66|95|99|67|68|96|97|98|63|73|93|91|92|94)[0-9]{7,12}$",
            message = "Не відповідає формату 380 ХХ 9999 999")
    private String phone;
    private Date registrationDate;
    private Integer totalOrders;
}
