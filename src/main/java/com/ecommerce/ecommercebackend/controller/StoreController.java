package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.request.store.StoreRegisterRequest;
import com.ecommerce.ecommercebackend.dto.request.user.LoginRequestDto;
import com.ecommerce.ecommercebackend.dto.response.order.OrderResponseDto;
import com.ecommerce.ecommercebackend.dto.response.product.ProductResponseDto;
import com.ecommerce.ecommercebackend.dto.response.store.StoreResponseDto;
import com.ecommerce.ecommercebackend.dto.response.store.StoreStatisticsDto;
import com.ecommerce.ecommercebackend.dto.response.user.LoginResponseDto;
import com.ecommerce.ecommercebackend.service.OrderService;
import com.ecommerce.ecommercebackend.service.ProductService;
import com.ecommerce.ecommercebackend.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final OrderService orderService;
    private final ProductService productService;

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<String> register(@ModelAttribute @Valid StoreRegisterRequest request) {
        storeService.registerStore(request);
        return ResponseEntity.ok("register");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        LoginResponseDto response = storeService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @PreAuthorize("authentication.name == authentication.name")
    public ResponseEntity<StoreResponseDto> getStoreInfo() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        StoreResponseDto dto = storeService.getCurrentStoreInfo();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            storeService.logout(token);
            return ResponseEntity.ok("Store logged out successfully");
        }
        return ResponseEntity.badRequest().body("No valid token provided");
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('STORE')")
    public ResponseEntity<List<OrderResponseDto>> getStoreOrders() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        List<OrderResponseDto> orders = orderService.getStoreOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('STORE')")
    public ResponseEntity<List<ProductResponseDto>> getStoreProducts() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        List<ProductResponseDto> products = productService.getAllProductsOfCurrentStore();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('STORE')")
    public ResponseEntity<StoreStatisticsDto> getStoreStatistics() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        Long orderCount = orderService.getStoreOrderCount();
        BigDecimal earnings = orderService.getStoreEarnings();
        List<ProductResponseDto> products = productService.getAllProductsOfCurrentStore();
        
        StoreStatisticsDto statistics = StoreStatisticsDto.builder()
                .totalOrders(orderCount)
                .totalEarnings(earnings)
                .totalProducts((long) products.size())
                .build();
                
        return ResponseEntity.ok(statistics);
    }
}