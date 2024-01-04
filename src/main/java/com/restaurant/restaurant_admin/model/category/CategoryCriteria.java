package com.restaurant.restaurant_admin.model.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CategoryCriteria {

    private String search;
    private String date;
    private Boolean status;

}
