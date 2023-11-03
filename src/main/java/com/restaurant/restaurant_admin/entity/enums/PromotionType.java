package com.restaurant.restaurant_admin.entity.enums;

public enum PromotionType {
    THIRD_PRODUCT_ON_GIFT("Третій товар подарунок"),
    PERCENT_FOR_CATEGORY("Відсоток для категорії"),
    PERCENT_FOR_PRODUCT("Відсток на продукт"),
    FIRST_BUY("Перша покупка"),
    PERCENT_OF_AMOUNT("Відсоток від мінімальної суми"),
    PERCENT_ON_BIRTHDAY("Відсоток на день народження"),
    FREE_DELIVERY_OF_AMOUNT("Безкоштовна доставка від мінімальної суми");
    public final String label;

    PromotionType(String label) {
        this.label = label;
    }
}
