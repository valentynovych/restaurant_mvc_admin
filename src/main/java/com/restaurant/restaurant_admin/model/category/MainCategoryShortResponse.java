package com.restaurant.restaurant_admin.model.category;

import lombok.Data;

import java.util.Date;

@Data
public class MainCategoryShortResponse {

    private Long id;
    private String categoryName;
    private Boolean isActive;
    private String previewIcon;
    private Integer topPosition;
    private Date dateOfCreate;
    private Integer countChildProduct;

    public MainCategoryShortResponse() {
    }

    public MainCategoryShortResponse(Long id, String categoryName, Boolean isActive, String previewIcon,
                                     Integer topPosition, Date dateOfCreate, Integer countChildProduct) {
        this.id = id;
        this.categoryName = categoryName;
        this.isActive = isActive;
        this.previewIcon = previewIcon;
        this.topPosition = topPosition;
        this.dateOfCreate = dateOfCreate;
        this.countChildProduct = countChildProduct;
    }
}
