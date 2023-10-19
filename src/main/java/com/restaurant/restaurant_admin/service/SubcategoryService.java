package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.model.SubcategoryDTO;
import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.mapper.SubcategoryMapper;
import com.restaurant.restaurant_admin.repository.SubcategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubcategoryService {
    private final SubcategoryRepo subcategoryRepo;

    public List<SubcategoryDTO> getSubcategoriesByParent(Long parentCategory) {
        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(parentCategory);
        return subcategoryRepo.findAllByParentCategory(mainCategory)
                .stream()
                .map(SubcategoryMapper.MAPPER::subcategoryToSubcategoryDTO)
                .toList();
    }
}
