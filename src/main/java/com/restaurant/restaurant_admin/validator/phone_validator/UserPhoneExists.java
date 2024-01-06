package com.restaurant.restaurant_admin.validator.phone_validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = {UserPhoneExistsValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserPhoneExists {
    String message() default "Номер вже використовується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
