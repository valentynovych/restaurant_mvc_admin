package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT pr.id, pr.name, pr.photo FROM ProductAndDishes pr " +
            "UNION " +
            "SELECT ing.id, ing.name, ing.photo FROM Ingredient ing")
    List<Product> findAllProductsAndIngredients();

    Page<Product> findAllProducts(Pageable pageable);
}
