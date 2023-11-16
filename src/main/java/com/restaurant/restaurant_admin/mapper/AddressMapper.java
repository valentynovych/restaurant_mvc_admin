package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.Address;
import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.model.AddressDTO;
import com.restaurant.restaurant_admin.model.UserShortResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressMapper MAPPER = Mappers.getMapper(AddressMapper.class);

    Address addressDtoToAddress(AddressDTO addressDTO);

    AddressDTO addressToAddressDto(Address address);

    default User userResponseToUser(UserShortResponse userShortResponse) {
        return Mappers.getMapper(UserMapper.class).userResponseToUser(userShortResponse);
    }

    default UserShortResponse userToUserResponse(User user) {
        return Mappers.getMapper(UserMapper.class).userToShortRequest(user);
    }
}
