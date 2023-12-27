package com.restaurant.restaurant_admin;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.entity.enums.Role;
import com.restaurant.restaurant_admin.repository.StaffRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

@SpringBootApplication
@RequiredArgsConstructor
public class RestaurantAdminApplication implements CommandLineRunner {

    private final StaffRepo staffRepo;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(RestaurantAdminApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<Staff> byEmail = staffRepo.findByEmail("admin@gmail.com");
        Staff staff = byEmail.orElse(new Staff());
        staff.setEmail("admin@gmail.com");
        staff.setPassword(passwordEncoder.encode("admin"));
        staff.setStatus(Boolean.TRUE);
        staff.setStaffRole(Role.ROLE_ADMIN);
        staff.setPhone("");
        staff.setFirstName("Root");
        staff.setLastName("Admin");
        staff.setDateOfBirth(new Date());
        staffRepo.save(staff);
    }
}
