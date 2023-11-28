package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.Order;
import com.restaurant.restaurant_admin.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepo extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    long countAllByUsedPromotion(Promotion promotion);
}
