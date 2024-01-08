package com.restaurant.restaurant_admin.validator.phone_validator;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.model.user.UserRequest;
import com.restaurant.restaurant_admin.repository.UserRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserPhoneExistsValidator implements ConstraintValidator<UserPhoneExists, UserRequest> {

    private final UserRepo userRepo;

    @Override
    public void initialize(UserPhoneExists annotation) {
    }

    @Override
    public boolean isValid(UserRequest userRequest, ConstraintValidatorContext constraintValidatorContext) {
        Optional<User> byUserDetailsPhone = userRepo.findByUserDetails_Phone(userRequest.getPhone());
        return byUserDetailsPhone.isEmpty() || byUserDetailsPhone.get().getId().equals(userRequest.getUserId());
    }

}
