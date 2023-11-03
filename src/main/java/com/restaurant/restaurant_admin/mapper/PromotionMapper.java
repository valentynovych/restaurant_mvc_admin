package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.PromotionType;
import com.restaurant.restaurant_admin.model.PromotionShort;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    PromotionMapper MAPPER = Mappers.getMapper(PromotionMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PromotionShort promotionToPromotionShort(Promotion promotion);
}
