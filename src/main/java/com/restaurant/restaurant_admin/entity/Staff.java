package com.restaurant.restaurant_admin.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Role staffRole;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean status;
    private String phone;
    private Date dateOgBirth;
    private String photo;
    @OneToMany(mappedBy = "orderPlaced")
    private List<Order> placedOrders;
}
