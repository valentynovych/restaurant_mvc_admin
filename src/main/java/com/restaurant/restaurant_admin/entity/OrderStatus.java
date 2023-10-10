package com.restaurant.restaurant_admin.entity;

public enum OrderStatus {
    NEW("Новий"),
    ACCEPTED("Прийнятий"),
    NOT_CONFIRMED_PREPARING("Не підтверджений"),
    PREPARING("Готується"),
    DELIVERED("Доставляється"),
    COMPLETED("Завершений"),
    CANCELED("Скасований");

    public final String label;

    OrderStatus(String label) {
        this.label = label;
    }
}
