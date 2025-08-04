package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.request.user.LoginRequestDto;
import com.ecommerce.ecommercebackend.dto.request.user.RegisterRequestDto;
import com.ecommerce.ecommercebackend.dto.response.user.LoginResponseDto;
import com.ecommerce.ecommercebackend.dto.response.user.RegisterResponseDto;
import com.ecommerce.ecommercebackend.entity.UserEntity;

import java.util.List;

public interface UserService {
    RegisterResponseDto register(RegisterRequestDto registerRequestDto);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    LoginResponseDto adminLogin(LoginRequestDto loginRequestDto);

    List<UserEntity> getAllUsers();

    UserEntity getCurrentUser();

    UserEntity updateCurrentUser(UserEntity userEntity);

    void deleteUser(Long id);

    Object getCurrentUserOrders();

    Object getCurrentUserAddresses();

    Object addUserAddress(Object address);

    Object getSystemStats();

    void logout(String token);
}