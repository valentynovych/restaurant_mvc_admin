package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsUserByEmail(String email);
    boolean existsUserByUserDetails_Phone(String phone);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserDetails_Phone(String phone);

}
