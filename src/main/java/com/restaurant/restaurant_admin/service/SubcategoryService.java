package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Subcategory;
import com.restaurant.restaurant_admin.mapper.MainCategoryMapper;
import com.restaurant.restaurant_admin.model.SubcategoryDTO;
import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.mapper.SubcategoryMapper;
import com.restaurant.restaurant_admin.repository.SubcategoryRepo;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.repository.specification.SubcategorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Qualifier
@RequiredArgsConstructor
@Log4j2
public class SubcategoryService {
    private final SubcategoryRepo subcategoryRepo;
    private final MainCategoryMapper mapper;

    public List<SubcategoryDTO> getSubcategoriesByParent(Long parentCategory) {
        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(parentCategory);
        return mapper.subcategoryListToDto(subcategoryRepo.findAllByParentCategory(mainCategory));
    }

    public Page<SubcategoryDTO> getSubcategories(int page, int pageSize, String search) {
        Pageable pageable = PageRequest.of(page, pageSize);
        SubcategorySpecification likeName = new SubcategorySpecification(
                new SearchCriteria("subcategoryName", ":", search));
        Page<Subcategory> subcategoryPage = subcategoryRepo.findAll(likeName, pageable);
        List<SubcategoryDTO> dtoList = mapper.subcategoryListToDto(subcategoryPage.getContent());
        Page<SubcategoryDTO> subcategoryDTOPage = new PageImpl<>(dtoList, pageable, subcategoryPage.getTotalElements());
        return subcategoryDTOPage;
    }
}
