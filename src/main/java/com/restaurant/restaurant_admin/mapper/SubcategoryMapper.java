package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import com.restaurant.restaurant_admin.entity.Subcategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubcategoryMapper {
    SubcategoryMapper MAPPER = Mappers.getMapper(SubcategoryMapper.class);

    @Mapping(target = "subcategoryId", source = "id")
    SubcategoryDTO subcategoryToSubcategoryDTO(Subcategory subcategory);

    @Mapping(target = "id", source = "subcategoryId")
    Subcategory dtoToSubcategory(SubcategoryDTO subcategoryDTO);
    @Mapping(target = "subcategoryId", source = "id")
    List<SubcategoryDTO> subcategoryListToDto(List<Subcategory> subcategories);
}
