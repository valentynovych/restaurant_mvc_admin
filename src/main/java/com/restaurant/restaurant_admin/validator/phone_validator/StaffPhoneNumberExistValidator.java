package com.restaurant.restaurant_admin.validator.phone_validator;

import com.restaurant.restaurant_admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StaffPhoneNumberExistValidator implements ConstraintValidator<StaffPhoneExists, String> {

    private final StaffRepo staffRepo;

    @Override
    public void initialize(StaffPhoneExists annotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !staffRepo.existsStaffByPhone(s);
    }
}
