package com.restaurant.restaurant_admin.validator;

import com.restaurant.restaurant_admin.model.category.MainCategoryDTO;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import com.restaurant.restaurant_admin.model.promotion.PromotionRequest;
import com.restaurant.restaurant_admin.model.category.SubcategoryDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class PromotionValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PromotionRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PromotionRequest request = (PromotionRequest) target;

        if (request.getName() == null || request.getName().isEmpty()) {
            errors.rejectValue(
                    "name",
                    "error.name",
                    "Обов'язкове поле");
        } else if (request.getName().length() > 100) {
            errors.rejectValue(
                    "name",
                    "error.name",
                    "Довжина поля не більше 100 символів");
        }
        if (request.getPhoto() != null && request.getPhoto().length() > 150) {
            errors.rejectValue(
                    "photo",
                    "error.photo",
                    "Назва файлу не більше 150 символів");
        }

        if (Objects.nonNull(request.getPromotionType())) {
            switch (request.getPromotionType()) {
                case FOR_PRODUCT -> validateProduct(errors, request.getForProduct(), "forProduct");
                case FOR_CATEGORY -> {
                    validateCategory(errors, request.getForCategory());
                    validateSubcategory(errors, request.getSubcategory());
                }
            }
        } else {
            ValidationUtils.rejectIfEmpty(
                    errors,
                    "promotionType",
                    "error.promotionType",
                    "Виберіть тип акції"
            );
        }

        if (Objects.nonNull(request.getPromotionCondition())) {
            switch (request.getPromotionCondition()) {
                case FIRST_BUY,
                        PERCENT_FOR_CATEGORY,
                        PERCENT_FOR_PRODUCT,
                        PERCENT_ON_BIRTHDAY -> validateDiscountAmount(errors, request.getDiscountAmount());
                case THIRD_PRODUCT_ON_GIFT -> validateProduct(errors, request.getGiftProduct(), "giftProduct");
                case FREE_DELIVERY_OF_AMOUNT -> validateMinimalAmount(errors, request.getMinimalAmount());
                case PERCENT_OF_AMOUNT -> {
                    validateDiscountAmount(errors, request.getDiscountAmount());
                    validateMinimalAmount(errors, request.getMinimalAmount());
                }
            }
        } else {
            ValidationUtils.rejectIfEmpty(
                    errors,
                    "promotionCondition",
                    "error.promotionCondition",
                    "Виберіть умови акції"
            );
        }

        if (request.getIsActive() == null) {
            errors.rejectValue(
                    "isActive",
                    "error.isActive",
                    "Хибне значення");
        }

        if (request.getPromoCode() != null && request.getPromoCode().length() > 30) {
            errors.rejectValue(
                    "promoCode",
                    "error.promoCode",
                    "Довжина промокоду до 30 символів");
        }

        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            errors.rejectValue(
                    "description",
                    "error.description",
                    "Обов'язкове поле");
        } else if (request.getDescription().length() > 200) {
            errors.rejectValue(
                    "description",
                    "error.description",
                    "Довжина поля не більше 200 символів");
        }
    }

    private void validateMinimalAmount(Errors errors, Integer minimalAmount) {
        if (minimalAmount == null) {
            errors.rejectValue(
                    "minimalAmount",
                    "error.minimalAmount",
                    "Обов'язкове поле");
        } else if (minimalAmount < 1) {
            errors.rejectValue("minimalAmount",
                    "error.minimalAmount",
                    "Мінімальна сума від 1 грн.");
        } else if (minimalAmount > 10_000) {
            errors.rejectValue("minimalAmount",
                    "error.minimalAmount",
                    "Мінімальна сума до 10 000 грн.");
        }
    }

    private void validateDiscountAmount(Errors errors, Integer discountAmount) {
        if (discountAmount == null) {
            errors.rejectValue(
                    "discountAmount",
                    "error.discountAmount",
                    "Обов'язкове поле");
        } else if (discountAmount < 1) {
            errors.rejectValue("discountAmount",
                    "error.discountAmount",
                    "Розмір знижки не може бути менше 1%");
        } else if (discountAmount > 99) {
            errors.rejectValue("discountAmount",
                    "error.discountAmount",
                    "Розмір знижки не може бути більше 99%");
        }
    }

    private void validateSubcategory(Errors errors, SubcategoryDTO subcategory) {
        if (Objects.isNull(subcategory)) {
            errors.rejectValue(
                    "subcategory.subcategoryId",
                    "error.subcategory.subcategoryId",
                    "Обов'язкове поле");
        } else if (subcategory.getSubcategoryId() == null || subcategory.getSubcategoryId() <= 0) {
            errors.rejectValue(
                    "subcategory.subcategoryId",
                    "error.subcategory.subcategoryId",
                    "Хибне значення поля");
        }
    }

    private void validateCategory(Errors errors, MainCategoryDTO forCategory) {
        if (Objects.isNull(forCategory)) {
            errors.rejectValue(
                    "forCategory.id",
                    "error.forCategory.id",
                    "Обов'язкове поле");
        } else if (forCategory.getId() == null || forCategory.getId() <= 0) {
            errors.rejectValue(
                    "forCategory.id",
                    "error.forCategory.id",
                    "Хибне значення поля");
        }
    }

    private void validateProduct(Errors errors, ProductShortResponse product, String fieldName) {
        if (Objects.isNull(product)) {
            errors.rejectValue(
                    fieldName + ".productId",
                    "error." + fieldName + ".productId",
                    "Обов'язкове поле");
        } else if (product.getProductId() == null || product.getProductId() <= 0) {
            errors.rejectValue(
                    fieldName + ".productId",
                    "error." + fieldName + ".productId",
                    "Хибне значення поля");
        }
    }


}
