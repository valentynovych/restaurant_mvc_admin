package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MainCategoryRepo extends JpaRepository<MainCategory, Long>, JpaSpecificationExecutor<MainCategory> {

    Optional<MainCategory> findById(Long id);
}
