package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.request.store.StoreRegisterRequest;
import com.ecommerce.ecommercebackend.dto.request.user.LoginRequestDto;
import com.ecommerce.ecommercebackend.dto.response.store.StoreResponseDto;
import com.ecommerce.ecommercebackend.dto.response.user.LoginResponseDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface StoreService {
    void registerStore(StoreRegisterRequest request);

    LoginResponseDto login(LoginRequestDto loginRequestDto);

    StoreResponseDto getCurrentStoreInfo() throws AccessDeniedException;

    List<StoreResponseDto> getAllStores();

    void deleteStore(Long id);

    void logout(String token);
}