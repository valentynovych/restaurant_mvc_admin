package com.restaurant.restaurant_admin.validator.promotionValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Constraint(validatedBy = PromotionConditionValidator.class)
//@Target(ElementType.TYPE)
//@Retention(RetentionPolicy.RUNTIME)
public @interface PromotionValidation {
    String message() default "Error";
    String field();
    Class<? extends Payload>[] payload() default {};
}
