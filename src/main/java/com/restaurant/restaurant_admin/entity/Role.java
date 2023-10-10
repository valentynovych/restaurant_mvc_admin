package com.restaurant.restaurant_admin.entity;

public enum Role {

    ADMIN("Головни адмін"),
    MANAGER("Адміністратор"),
    COURIER("Курьєр"),
    ACCOUNTANT("Бухгалтер");
    public final String label;

    Role(String label) {
        this.label = label;
    }
}
