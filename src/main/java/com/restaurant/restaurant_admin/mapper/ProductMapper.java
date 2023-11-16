package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.MainCategory;
import com.restaurant.restaurant_admin.entity.Product;
import com.restaurant.restaurant_admin.model.IngredientRequest;
import com.restaurant.restaurant_admin.model.ProductRequest;
import com.restaurant.restaurant_admin.model.ProductResponse;
import com.restaurant.restaurant_admin.model.ProductShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "productId", source = "id")
    ProductShortResponse productToShortResponse(Product product);
    @Mapping(target = "id", source = "productId")
    Product shortResponseToProduct(ProductShortResponse product);

    @Mapping(target = "productId", source = "id")
    List<ProductShortResponse> productListToShortResponseList(List<Product> products);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "subcategory.subcategoryId", source = "subcategory.id")
    @Mapping(target = "isActive", source = "isActive")
    ProductResponse productToProductResponse(Product product);

    @Mapping(target = "id", source = "productId")
    @Mapping(target = "promotion.id", source = "promotion")
    @Mapping(target = "mainCategory.id", source = "mainCategory")
    @Mapping(target = "subcategory.id", source = "subcategory")
    @Mapping(target = "consistsOfIngredients", source = "consistsOfIngredients", qualifiedByName = "longToProduct")
    Product productRequestToProduct(ProductRequest productRequest);

    @Mapping(target = "id", source = "productId")
    @Mapping(target = "forMainCategory", source = "forMainCategory", qualifiedByName = "longToMainCategory")
    Product ingredientReguestToProduct(IngredientRequest productRequest);

    @Named(value = "longToProduct")
    static Product longToProduct(Long id) {
        Product product = new Product();
        product.setId(id);
        return product;
    }

    @Named(value = "longToMainCategory")
    static MainCategory longToMainCategory(Long id) {
        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(id);
        return mainCategory;
    }
}
