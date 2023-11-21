package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.Subcategory;
import com.restaurant.restaurant_admin.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    PromotionMapper MAPPER = Mappers.getMapper(PromotionMapper.class);

    PromotionShort promotionToPromotionShort(Promotion promotion);

    PromotionResponse promotionToPromotionResponse(Promotion promotion);

    List<PromotionResponse> listPromotionToPromotionResponse(List<Promotion> promotions);

    Promotion promotionResponseToPromotion(PromotionResponse promotion);

    Promotion promotionRequestToPromotion(PromotionRequest promotionRequest);

    PromotionRequest promotionToPromotionRequest(Promotion promotion);

    default ProductShortResponse productToProductShortResponse(Product product) {
        return Mappers.getMapper(ProductMapper.class).productToShortResponse(product);
    }

    default Product productShortResponseToProduct(ProductShortResponse product) {
        return Mappers.getMapper(ProductMapper.class).shortResponseToProduct(product);
    }

    default SubcategoryDTO subcategoryToDto(Subcategory subcategory) {
        return Mappers.getMapper(SubcategoryMapper.class).subcategoryToSubcategoryDTO(subcategory);
    }

    default Subcategory subcategoryDtoToSubcategory(SubcategoryDTO subcategory) {
        return Mappers.getMapper(SubcategoryMapper.class).dtoToSubcategory(subcategory);
    }

    default MainCategoryDTO mainCategoryToDto(MainCategory mainCategory) {
        return Mappers.getMapper(MainCategoryMapper.class).mainCategoryToMainCategoryDto(mainCategory);
    }

    default MainCategory mainCategoryDtoToMainCategory(MainCategoryDTO mainCategory) {
        return Mappers.getMapper(MainCategoryMapper.class).mainCategoryDtoToMainCategory(mainCategory);
    }
}
