package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StaffRepo extends JpaRepository<Staff, Long>, JpaSpecificationExecutor<Staff> {

    Optional<Staff> findByEmail(String  email);
    @Query("SELECT s.password FROM Staff s WHERE s.id=:id")
    String getPasswordById(Long id);

    boolean existsStaffByEmail(String email);
    boolean existsStaffByPhone(String phone);

    Optional<Staff> findByPhone(String phone);
}
