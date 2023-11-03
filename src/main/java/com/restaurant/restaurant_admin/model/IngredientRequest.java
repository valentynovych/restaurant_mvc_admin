package com.restaurant.restaurant_admin.model;

import com.restaurant.restaurant_admin.entity.enums.IngredientCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IngredientRequest {
    private Long productId;
    @NotEmpty(message = "Обов'язкове поле")
    @Size(max = 100, message = "Довжина не більше 100 символів")
    private String name;
    @NotNull(message = "Допустимі значення активний/не активний")
    private Boolean isActive;
    @NotNull(message = "Допустимі значення активний/не активний")
    private Boolean isIngredient;
    @NotNull(message = "Обов'язкове поле")
    @DecimalMin(value = "0.01", message = "Ціна має бути більше 0.01 грн")
    @DecimalMax(value = "10000", message = "Ціна має бути менше 10 000 грн")
    private BigDecimal price;
    @NotNull(message = "Обов'язкове поле")
    @Min(value = 1, message = "Вага має бути більше 1 гр")
    @Max(value = 50_000, message = "Вага має бути більше 50 кг")
    private Integer weight;
    @Size(max = 100, message = "Довжина не більше 100 символів")
    private String photo;
    @NotEmpty(message = "Обов'язкове поле")
    private List<Long> forMainCategory;
    @NotNull(message = "Обов'язкове поле")
    @Enumerated(EnumType.STRING)
    private IngredientCategory ingredientCategory;
    private MultipartFile photoFile;
}
