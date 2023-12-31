package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.model.user.UserShortDetails;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", source = "userDetails.firstName")
    @Mapping(target = "lastName", source = "userDetails.lastName")
    @Mapping(target = "phone", source = "userDetails.phone")
    @Mapping(target = "registrationDate", source = "userDetails.registrationDate")
    UserShortResponse userToShortRequest(User user);

    List<UserShortResponse> userListToShortResponceList(List<User> users);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "firstName", source = "userDetails.firstName")
    @Mapping(target = "lastName", source = "userDetails.lastName")
    @Mapping(target = "phone", source = "userDetails.phone")
    @Mapping(target = "dateOfBirth", source = "userDetails.dateOfBirth")
    @Mapping(target = "activeBonuses", source = "userDetails.activeBonuses")
    @Mapping(target = "usedBonuses", source = "userDetails.usedBonuses")
    UserShortDetails userToShortDetails(User user);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "firstName", target = "userDetails.firstName")
    @Mapping(source = "lastName", target = "userDetails.lastName")
    @Mapping(source = "phone", target = "userDetails.phone")
    @Mapping(source = "registrationDate", target = "userDetails.registrationDate")
    User userResponseToUser(UserShortResponse userShortResponse);
}
