package com.restaurant.restaurant_admin.model.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.restaurant_admin.entity.MainCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubcategoryDTO {
    private Long subcategoryId;
    @NotBlank(message = "Поле не може бути пустим")
    @Size(max = 100, message = "Назвна повинна бути не довша 100 символів")
    private String subcategoryName;
    @JsonIgnore
    private MainCategory parentCategory;

    public SubcategoryDTO() {
    }

    public SubcategoryDTO(Long subcategoryId, String subcategoryName, MainCategory parentCategory) {
        this.subcategoryId = subcategoryId;
        this.subcategoryName = subcategoryName;
        this.parentCategory = parentCategory;
    }
}
