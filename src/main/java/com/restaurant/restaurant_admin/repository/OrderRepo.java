package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findOrderByDatetimeOfCreateAfter(Instant lastMonth);
}
