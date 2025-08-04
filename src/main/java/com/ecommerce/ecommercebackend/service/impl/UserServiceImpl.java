package com.ecommerce.ecommercebackend.service.impl;

import com.ecommerce.ecommercebackend.dto.request.user.LoginRequestDto;
import com.ecommerce.ecommercebackend.dto.request.user.RegisterRequestDto;
import com.ecommerce.ecommercebackend.dto.response.user.LoginResponseDto;
import com.ecommerce.ecommercebackend.dto.response.user.RegisterResponseDto;
import com.ecommerce.ecommercebackend.entity.UserEntity;
import com.ecommerce.ecommercebackend.enums.Roles;
import com.ecommerce.ecommercebackend.mapper.UserMapper;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import com.ecommerce.ecommercebackend.security.jwt.JwtService;
import com.ecommerce.ecommercebackend.security.user.UserPrincipal;
import com.ecommerce.ecommercebackend.service.TokenBlacklistService;
import com.ecommerce.ecommercebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new RuntimeException("email exist");
        }

        if (!registerRequestDto.getPassword().equals(registerRequestDto.getPasswordConfirm())) {
            throw new RuntimeException("passwords do not match");
        }

        UserEntity user = userMapper.mapToUser(registerRequestDto);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(Roles.USER));
        }

        userRepository.save(user);

        RegisterResponseDto response = userMapper.mapToRegisterResponseDto(user);
        response.setMessage("User registered successfully");
        return response;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserEntity user = userRepository.findByEmail(loginRequestDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserPrincipal userPrincipal = new UserPrincipal(user);
            String token = jwtService.generateToken(userPrincipal);

            return new LoginResponseDto(token, "Bearer");
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    @Override
    public LoginResponseDto adminLogin(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserEntity user = userRepository.findByEmail(loginRequestDto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getRoles().contains(Roles.ADMIN)) {
                throw new RuntimeException("Access denied: Admin role required");
            }

            UserPrincipal userPrincipal = new UserPrincipal(user);
            String token = jwtService.generateToken(userPrincipal);

            return new LoginResponseDto(token, "Bearer");
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserEntity updateCurrentUser(UserEntity userEntity) {
        UserEntity currentUser = getCurrentUser();
        if (userEntity.getGender() != null) {
            currentUser.setGender(userEntity.getGender());
        }
        currentUser.setConsentMarketing(userEntity.isConsentMarketing());
        currentUser.setConsentMessagesDelivered(userEntity.isConsentMessagesDelivered());
        currentUser.setConsentMembershipAgreement(userEntity.isConsentMembershipAgreement());
        return userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Object getCurrentUserOrders() {
        Map<String, Object> orders = new HashMap<>();
        orders.put("message", "Orders functionality will be implemented later");
        orders.put("orders", List.of());
        return orders;
    }

    @Override
    public Object getCurrentUserAddresses() {
        Map<String, Object> addresses = new HashMap<>();
        addresses.put("message", "Addresses functionality will be implemented later");
        addresses.put("addresses", List.of());
        return addresses;
    }

    @Override
    public Object addUserAddress(Object address) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Address functionality will be implemented later");
        response.put("address", address);
        return response;
    }

    @Override
    public Object getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("message", "More stats will be implemented later");
        return stats;
    }

    @Override
    public void logout(String token) {
        tokenBlacklistService.blacklistToken(token);
        SecurityContextHolder.clearContext();
    }
}
