package com.restaurant.restaurant_admin.validator.email_validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StaffEmailExistValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StaffEmailExists {
    String email() default "email";

    String message() default "Email вже використавується";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
