package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.request.user.LoginRequestDto;
import com.ecommerce.ecommercebackend.dto.response.product.ProductResponseDto;
import com.ecommerce.ecommercebackend.dto.response.store.StoreResponseDto;
import com.ecommerce.ecommercebackend.dto.response.user.LoginResponseDto;
import com.ecommerce.ecommercebackend.entity.UserEntity;
import com.ecommerce.ecommercebackend.service.ProductService;
import com.ecommerce.ecommercebackend.service.StoreService;
import com.ecommerce.ecommercebackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final StoreService storeService;
    private final ProductService productService;

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<LoginResponseDto> adminLogin(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        LoginResponseDto response = userService.adminLogin(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreResponseDto>> getAllStores() {
        List<StoreResponseDto> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/stores/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getSystemStats() {
        Object stats = userService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token);
            return ResponseEntity.ok("Admin logged out successfully");
        }
        return ResponseEntity.badRequest().body("No valid token provided");
    }
}