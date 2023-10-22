package com.restaurant.restaurant_admin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MainCategoryDTO {

    private Long id;
    @NotBlank(message = "Поле не може бути пустим")
    @NotNull(message = "Поле не може бути пустим")
    @Size(max = 100, message = "Назва від 4 до 100 символів")
    private String categoryName;
    @NotNull(message = "Не валідне значення")
    private Boolean isActive;
    @Size(max = 200, message = "Може містити не більше 200 символів")
    private String previewIcon;
    @Max(value = 999, message = "Не більше 3-х значного числа")
    private Integer topPosition;
    private Date dateOfCreate;
    @Max(value = 9999, message = "Не більше 4-х значного числа")
    private Integer countChildProduct;
    @JsonIgnore
    @Valid
    private List<SubcategoryDTO> subcategories;

    public MainCategoryDTO() {
    }

    public MainCategoryDTO(Long id, String categoryName, Boolean isActive, String previewIcon,
                           Integer topPosition, Date dateOfCreate, Integer countChildProduct,
                           List<SubcategoryDTO> subcategories) {
        this.id = id;
        this.categoryName = categoryName;
        this.isActive = isActive;
        this.previewIcon = previewIcon;
        this.topPosition = topPosition;
        this.dateOfCreate = dateOfCreate;
        this.countChildProduct = countChildProduct;
        this.subcategories = subcategories;
    }
}
