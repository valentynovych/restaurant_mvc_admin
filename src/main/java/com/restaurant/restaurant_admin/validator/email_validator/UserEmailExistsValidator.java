package com.restaurant.restaurant_admin.validator.email_validator;

import com.restaurant.restaurant_admin.repository.UserRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserEmailExistsValidator implements ConstraintValidator<UserEmailExists, String> {

    private final UserRepo userRepo;

    @Override
    public void initialize(UserEmailExists annotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !userRepo.existsUserByEmail(s);
    }
}
