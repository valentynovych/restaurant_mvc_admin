package com.restaurant.restaurant_admin.validator.email_validator;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.model.user.UserRequest;
import com.restaurant.restaurant_admin.repository.UserRepo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserEmailExistsValidator implements ConstraintValidator<UserEmailExists, UserRequest> {

    private final UserRepo userRepo;

    @Override
    public boolean isValid(UserRequest userRequest, ConstraintValidatorContext constraintValidatorContext) {
        Long idValue = userRequest.getUserId();
        String emailValue = userRequest.getEmail();
        Optional<User> byId = userRepo.findByEmail(emailValue);

        return byId.isEmpty() || byId.get().getId().equals(idValue);
    }
}
