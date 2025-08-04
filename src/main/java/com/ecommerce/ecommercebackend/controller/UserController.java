package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.entity.UserEntity;
import com.ecommerce.ecommercebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserEntity> getCurrentUserProfile() {
        UserEntity user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserEntity> updateProfile(@RequestBody UserEntity userEntity) {
        UserEntity updatedUser = userService.updateCurrentUser(userEntity);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/orders")
    public ResponseEntity<Object> getUserOrders() {
        Object orders = userService.getCurrentUserOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/addresses")
    public ResponseEntity<Object> getUserAddresses() {
        Object addresses = userService.getCurrentUserAddresses();
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/addresses")
    public ResponseEntity<Object> addUserAddress(@RequestBody Object address) {
        Object newAddress = userService.addUserAddress(address);
        return ResponseEntity.ok(newAddress);
    }
}