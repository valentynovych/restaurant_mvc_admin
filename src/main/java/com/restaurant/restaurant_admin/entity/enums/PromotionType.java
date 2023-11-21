package com.restaurant.restaurant_admin.entity.enums;

public enum PromotionType {
    FOR_PRODUCT("Для одного товару"),
    FOR_CATEGORY("Для категорії товарів");
    public final String label;

    PromotionType(String label) {
        this.label = label;
    }
}
