package com.restaurant.restaurant_admin.repository;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Subcategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepo extends CrudRepository<Subcategory, Long> {

    List<Subcategory> findAllByParentCategory(MainCategory parentCategory);
}
