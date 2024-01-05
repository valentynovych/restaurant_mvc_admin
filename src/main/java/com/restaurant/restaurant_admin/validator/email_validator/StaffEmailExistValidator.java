package com.restaurant.restaurant_admin.validator.email_validator;

import com.restaurant.restaurant_admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StaffEmailExistValidator implements ConstraintValidator<StaffEmailExists, String> {

    private final StaffRepo staffRepo;

    @Override
    public void initialize(StaffEmailExists annotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !staffRepo.existsStaffByEmail(s);
    }
}
