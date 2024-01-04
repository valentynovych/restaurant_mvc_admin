package com.restaurant.restaurant_admin.mapper;

import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.model.staff.StaffRequest;
import com.restaurant.restaurant_admin.model.staff.StaffResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffMapper {

    StaffMapper MAPPER = Mappers.getMapper(StaffMapper.class);

    @Mapping(target = "staffId", source = "id")
    @Mapping(target = "photoName", source = "photo")
    StaffResponse staffToModelResponse(Staff staff);
    @Mapping(target = "id", source = "staffId")
    @Mapping(target = "photo", source = "photoName")
    Staff responseToStaff(StaffResponse staffResponse);
    @Mapping(target = "staffId", source = "id")
    @Mapping(target = "photoName", source = "photo")
    List<StaffResponse> staffListToModelResponseList(List<Staff> staffList);
    @Mapping(target = "id", source = "staffId")
    @Mapping(target = "photo", source = "photoName")
    Staff requestToStaff(StaffRequest staffRequest);
}
