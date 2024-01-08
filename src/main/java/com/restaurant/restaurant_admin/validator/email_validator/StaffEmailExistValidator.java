package com.restaurant.restaurant_admin.validator.email_validator;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.model.staff.StaffRequest;
import com.restaurant.restaurant_admin.repository.StaffRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class StaffEmailExistValidator implements ConstraintValidator<StaffEmailExists, StaffRequest> {

    private final StaffRepo staffRepo;

    @Override
    public void initialize(StaffEmailExists annotation) {
    }

    @Override
    public boolean isValid(StaffRequest staffRequest, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Staff> byEmail = staffRepo.findByEmail(staffRequest.getEmail());
        return byEmail.isEmpty() || byEmail.get().getId().equals(staffRequest.getStaffId());
    }
}
