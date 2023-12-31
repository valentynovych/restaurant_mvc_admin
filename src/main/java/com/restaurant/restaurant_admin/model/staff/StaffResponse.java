package com.restaurant.restaurant_admin.model.staff;

import com.restaurant.restaurant_admin.entity.enums.Role;
import lombok.Data;

import java.util.Date;

@Data
public class StaffResponse {

    private Long staffId;
    private Role staffRole;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean status;
    private String phone;
    private Date dateOfBirth;
    private String photoName;
}
