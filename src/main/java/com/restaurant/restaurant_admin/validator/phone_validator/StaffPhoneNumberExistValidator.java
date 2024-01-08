package com.restaurant.restaurant_admin.validator.phone_validator;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.model.staff.StaffRequest;
import com.restaurant.restaurant_admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class StaffPhoneNumberExistValidator implements ConstraintValidator<StaffPhoneExists, StaffRequest> {

    private final StaffRepo staffRepo;

    @Override
    public boolean isValid(StaffRequest staffRequest, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Staff> byPhone = staffRepo.findByPhone(staffRequest.getPhone());

        return byPhone.isEmpty() || byPhone.get().getId().equals(staffRequest.getStaffId());
    }
}
