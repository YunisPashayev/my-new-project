package com.ecommerce.ecommercebackend.mapper;

import com.ecommerce.ecommercebackend.dto.request.user.RegisterRequestDto;
import com.ecommerce.ecommercebackend.dto.response.user.RegisterResponseDto;
import com.ecommerce.ecommercebackend.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "surname", target = "surname") 
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "gender", target = "gender")
    UserEntity mapToUser(RegisterRequestDto registerRequestDto);

    @Mapping(target = "message", ignore = true)
    RegisterResponseDto mapToRegisterResponseDto(UserEntity user);
}