package com.restaurant.restaurant_admin.model.product;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    private Long productId;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 100, message = "Довжина не більше 100 символів")
    private String name;
    @NotNull(message = "Допустимі значення активний/не активний")
    private Boolean isActive;
    @NotNull(message = "Допустимі значення активний/не активний")
    private Boolean isIngredient;
    @NotNull(message = "Обов'язкове поле")
    @DecimalMin(value = "0.01", inclusive = false, message = "Ціна має бути більше 0.01 грн")
    @DecimalMax(value = "10000", inclusive = false, message = "Ціна має бути менше 10 000 грн")
//    @Digits(integer = 5, fraction = 2)
    private BigDecimal price;
    @NotNull(message = "Обов'язкове поле")
    @Min(value = 1, message = "Вага має бути більше 1 гр")
    @Max(value = 50_000, message = "Вага має бути більше 50 кг")
    private Integer weight;
    @Size(max = 100, message = "Довжина не більше 100 символів")
    private String photo;
    @NotNull(message = "Допустимі значення активний/не активний")
    private Boolean isNovelty;
    @Size(max = 200, message = "Довжина не більше 200 символів")
    private String characteristics;
    @Size(max = 200, message = "Довжина не більше 200 символів")
    private String description;
    @NotNull(message = "Допустимі значення активний/не активний")
    private Boolean promotionIsActive;
    private Long promotion;
    @NotNull(message = "Обов'язкове поле")
    private Long mainCategory;
    @NotNull(message = "Обов'язкове поле")
    private Long subcategory;
    @NotEmpty(message = "Продукт має містити принаймні 1 інгредієнт")
    private List<Long> consistsOfIngredients;
    private MultipartFile photoFile;
}
