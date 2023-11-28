package com.restaurant.restaurant_admin.model;

import com.restaurant.restaurant_admin.entity.Address;
import com.restaurant.restaurant_admin.entity.Promotion;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class OrderRequest {

    private Long orderId;
    @Valid
    private UserShortResponse user;
    @NotNull(message = "Статус замовлення обов'язковий")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<PromotionResponse> usedPromotion;
    private Integer usedBonuses;
    @NotEmpty(message = "Обов'язкове поле")
    private String payment;
    @Valid
    private AddressDTO address;
    @NotNull(message = "Обов'язкове поле")
    @Min(value = 1, message = "> 1хв")
    private Integer deliveryTime;
    @Size(max = 10, message = "Завелике значення")
    private String reservedTime;
    @Size(max = 200, message = "Не довше 200 символів")
    private String comment;
    private String cutlery;
    private StaffResponse orderPlaced;
}
