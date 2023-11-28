package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.*;
import com.restaurant.restaurant_admin.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper extends ProductMapper {
    OrderMapper MAPPER = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderId", source = "id")
    OrderShortResponse orderToShortResponse(Order order);

    @Mapping(target = "orderId", source = "id")
    List<OrderShortResponse> listOrderToShortResponseList(List<Order> content);

    @Mapping(target = "orderId", source = "id")
    OrderResponse orderToResponse(Order order);


    @Mapping(target = "product.id", source = "product.productId")
    @Mapping(target = "order.id", source = "order.orderId")
    OrderItem itemResponseToOrderItem(OrderItemRequest orderItemRequest);

    @Mapping(target = "id", source = "orderId")
    @Mapping(target = "datetimeOfCreate", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order requestToOrder(OrderRequest orderRequest);

    Set<PromotionResponse> setPromotionToSetResponse(Set<Promotion> promotions);
    Set<Promotion> setResponseToSetPromotion(Set<PromotionResponse> responseSet);

    default StaffResponse staffToResponse(Staff staff) {
        return Mappers.getMapper(StaffMapper.class).staffToModelResponse(staff);
    }

    default Staff staffResponseToStaff(StaffResponse staffResponse) {
        return Mappers.getMapper(StaffMapper.class).responseToStaff(staffResponse);
    }

    default User userResponseToUser(UserShortResponse userShortResponse) {
        return Mappers.getMapper(UserMapper.class).userResponseToUser(userShortResponse);
    }

    default UserShortResponse userToShortResponse(User user) {
        return Mappers.getMapper(UserMapper.class).userToShortRequest(user);
    }

    default PromotionResponse promotionToPromotionResponse(Promotion promotion) {
        return Mappers.getMapper(PromotionMapper.class).promotionToPromotionResponse(promotion);
    }

    default Promotion promotionResponseToPromotion(PromotionResponse promotionResponse) {
        return Mappers.getMapper(PromotionMapper.class).promotionResponseToPromotion(promotionResponse);
    }
}
