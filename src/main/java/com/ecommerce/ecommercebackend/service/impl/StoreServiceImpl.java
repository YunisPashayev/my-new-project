package com.ecommerce.ecommercebackend.service.impl;

import com.ecommerce.ecommercebackend.dto.request.store.StoreRegisterRequest;
import com.ecommerce.ecommercebackend.dto.request.user.LoginRequestDto;
import com.ecommerce.ecommercebackend.dto.response.store.StoreResponseDto;
import com.ecommerce.ecommercebackend.dto.response.user.LoginResponseDto;
import com.ecommerce.ecommercebackend.entity.StoreEntity;
import com.ecommerce.ecommercebackend.mapper.StoreMapper;
import com.ecommerce.ecommercebackend.repository.StoreRepository;
import com.ecommerce.ecommercebackend.security.jwt.JwtService;
import com.ecommerce.ecommercebackend.security.store.StorePrincipal;
import com.ecommerce.ecommercebackend.security.util.StoreSecurityUtil;
import com.ecommerce.ecommercebackend.service.TokenBlacklistService;
import com.ecommerce.ecommercebackend.service.StoreService;
import com.ecommerce.ecommercebackend.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final StoreSecurityUtil storeSecurityUtil;
    private final CloudinaryService cloudinaryService;
    private final TokenBlacklistService tokenBlacklistService;

    public void registerStore(StoreRegisterRequest request) {
        if (storeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        } else if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        } else if (Boolean.TRUE.equals(request.getAgreedToTerms())) {
            StoreEntity store = storeMapper.mapToStoreEntity(request);
            store.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            store = storeRepository.save(store);

            String storeFolder = "image/store_" + store.getId();

            if (request.getLogo() != null && !request.getLogo().isEmpty()) {
                String logoUrl = cloudinaryService.uploadFile(request.getLogo(), storeFolder + "/logo", "logo");
                store.setLogo(logoUrl);
            }

            if (request.getBanner() != null && !request.getBanner().isEmpty()) {
                String bannerUrl = cloudinaryService.uploadFile(request.getBanner(), storeFolder + "/banner", "banner");
                store.setBanner(bannerUrl);
            }

            storeRepository.save(store);
        } else {
            throw new IllegalArgumentException("You must agree to terms");
        }
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        if(authentication.isAuthenticated()){
            StoreEntity store = storeRepository.findStoreEntitiesByEmail(loginRequestDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Store not found"));


            StorePrincipal storePrincipal = new StorePrincipal(store);
            String token = jwtService.generateToken(storePrincipal);

            return new LoginResponseDto(token,"Bearer");

        }

        throw new RuntimeException("Authentication failed");
    }

    public StoreResponseDto getCurrentStoreInfo() throws AccessDeniedException {
        StoreEntity store = storeSecurityUtil.getCurrentStore();
        return storeMapper.mapToStoreResponse(store);
    }

    @Override
    public List<StoreResponseDto> getAllStores() {
        List<StoreEntity> stores = storeRepository.findAll();
        return stores.stream()
                .map(storeMapper::mapToStoreResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    @Override
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
        SecurityContextHolder.clearContext();
    }
}
