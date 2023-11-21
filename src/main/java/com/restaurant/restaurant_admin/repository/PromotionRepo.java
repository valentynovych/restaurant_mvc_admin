package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepo extends JpaRepository<Promotion, Long>, JpaSpecificationExecutor<Promotion> {
}
