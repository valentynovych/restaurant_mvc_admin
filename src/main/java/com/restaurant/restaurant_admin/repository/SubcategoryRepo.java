package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepo extends JpaRepository<Subcategory, Long>, JpaSpecificationExecutor<Subcategory> {

    List<Subcategory> findAllByParentCategory(MainCategory parentCategory);
}
