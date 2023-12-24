package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Subcategory;
import com.restaurant.restaurant_admin.mapper.MainCategoryMapper;
import com.restaurant.restaurant_admin.model.SubcategoryDTO;
import com.restaurant.restaurant_admin.repository.SubcategoryRepo;
import com.restaurant.restaurant_admin.repository.specification.SearchCriteria;
import com.restaurant.restaurant_admin.repository.specification.SubcategorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SubcategoryService {
    private final SubcategoryRepo subcategoryRepo;

    public List<SubcategoryDTO> getSubcategoriesByParent(Long parentCategory) {
        log.info("getSubcategoriesByParent() -> start, with parent category id: " + parentCategory);
        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(parentCategory);
        List<Subcategory> allByParentCategory = subcategoryRepo.findAllByParentCategory(mainCategory);
        List<SubcategoryDTO> subcategoryDTOS = MainCategoryMapper.MAPPER.subcategoryListToDto(allByParentCategory);
        log.info("getSubcategoriesByParent() -> exit, return list size: " + subcategoryDTOS.size());

        return subcategoryDTOS;
    }

    public Page<SubcategoryDTO> getSubcategories(int page, int pageSize, String search) {
        log.info("getSubcategories() -> start, with search: " + search);
        Pageable pageable = PageRequest.of(page, pageSize);
        SubcategorySpecification likeName = new SubcategorySpecification(
                new SearchCriteria("subcategoryName", ":", search));
        Page<Subcategory> subcategoryPage = subcategoryRepo.findAll(likeName, pageable);
        List<SubcategoryDTO> dtoList = MainCategoryMapper.MAPPER.subcategoryListToDto(subcategoryPage.getContent());
        Page<SubcategoryDTO> subcategoryDTOPage = new PageImpl<>(dtoList, pageable, subcategoryPage.getTotalElements());
        log.info("getSubcategories() -> exit");
        return subcategoryDTOPage;
    }
}
