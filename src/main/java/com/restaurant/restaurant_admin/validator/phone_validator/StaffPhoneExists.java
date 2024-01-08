package com.restaurant.restaurant_admin.validator.phone_validator;

import com.restaurant.restaurant_admin.validator.email_validator.StaffEmailExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {StaffPhoneNumberExistValidator.class})
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StaffPhoneExists {
    String phone() default "phone";
    String message() default "Номер вже використовується";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
