package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.request.order.CreateOrderRequestDto;
import com.ecommerce.ecommercebackend.dto.response.order.OrderResponseDto;
import com.ecommerce.ecommercebackend.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/orders")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@RequiredArgsConstructor

public class OrderController {

    private final OrderService orderService;


    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid CreateOrderRequestDto createOrderRequestDto) {
        log.info("Creating new order from cart");
        OrderResponseDto response = orderService.createOrderFromCart(createOrderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders() {
        List<OrderResponseDto> orders = orderService.getUserOrders();
        return ResponseEntity.ok(orders);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
        @PathVariable Long orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }


    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, String>> cancelOrder(
        @PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(Map.of("status", "cancelled", "message", "Order cancelled successfully"));
    }


    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


    @PutMapping("/admin/{orderId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
        @PathVariable Long orderId,
        @PathVariable String status) {
        OrderResponseDto response = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(response);
    }
}