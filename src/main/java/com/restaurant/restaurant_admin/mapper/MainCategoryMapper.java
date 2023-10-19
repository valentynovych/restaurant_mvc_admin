package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.model.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.MainCategoryTablesResponse;
import com.restaurant.restaurant_admin.entity.MainCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MainCategoryMapper extends SubcategoryMapper{
    MainCategoryMapper MAPPER = Mappers.getMapper(MainCategoryMapper.class);

    MainCategoryDTO mainCategoryToMainCategoryDto(MainCategory mainCategory);

    MainCategory mainCategoryDtoToMainCategory(MainCategoryDTO mainCategory);

    MainCategoryTablesResponse toMainCategoryTablesResponse(MainCategory mainCategory);

    List<MainCategoryDTO> listMainCategoryToDtoList(List<MainCategory> mainCategories);

}
