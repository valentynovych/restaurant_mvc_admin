package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "user_details")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private Date dateOfBirth;
    private String photo;
    private Integer activeBonuses;
    private Integer usedBonuses;
    private Date registrationDate;

}
