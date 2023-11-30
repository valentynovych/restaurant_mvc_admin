package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.OrderItem;
import com.restaurant.restaurant_admin.model.PopularCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findDistinctTopByProduct_MainCategory_CountChildProduct(int top);
    @Query("SELECT new com.restaurant.restaurant_admin.model.PopularCategory(mc.categoryName, COUNT(*)) FROM OrderItem oi " +
            "JOIN Product pr ON oi.product.id = pr.id " +
            "JOIN MainCategory mc on mc.id = pr.mainCategory.id " +
            "GROUP BY mc.id, mc.categoryName")
    List<PopularCategory> findCountUsedMainCategoryInOrders();
}
