package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.request.cart.AddToCartRequestDto;
import com.ecommerce.ecommercebackend.dto.request.cart.UpdateCartItemQuantityRequestDto;
import com.ecommerce.ecommercebackend.dto.response.cart.CartResponseDto;
import com.ecommerce.ecommercebackend.dto.response.cart.CartSummaryDto;
import com.ecommerce.ecommercebackend.service.CartService;

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
@RequestMapping("/cart")
@RequiredArgsConstructor

public class CartController {

    private final CartService cartService;


    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody @Valid AddToCartRequestDto addToCartRequestDto) {
        log.info("Adding product {} to cart", addToCartRequestDto.getProductId());
        CartResponseDto response = cartService.addToCart(addToCartRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/items")
    public ResponseEntity<List<CartResponseDto>> getCartItems() {
        List<CartResponseDto> cartItems = cartService.getCartItems();
        return ResponseEntity.ok(cartItems);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<CartSummaryDto> getCartSummary() {
        CartSummaryDto summary = cartService.getCartSummary();
        return ResponseEntity.ok(summary);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponseDto> getCartItem(
        @PathVariable Long cartItemId) {
        CartResponseDto cartItem = cartService.getCartItem(cartItemId);
        return ResponseEntity.ok(cartItem);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/items/{cartItemId}/quantity")
    public ResponseEntity<CartResponseDto> updateQuantity(
        @PathVariable Long cartItemId,
        @RequestBody @Valid UpdateCartItemQuantityRequestDto requestDto) {
        CartResponseDto response = cartService.updateCartItemQuantity(cartItemId, requestDto);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
        @PathVariable Long cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> removeProductFromCart(
        @PathVariable Long productId) {
        cartService.removeProductFromCart(productId);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/products/{productId}/exists")
    public ResponseEntity<Map<String, Boolean>> isProductInCart(
        @PathVariable Long productId) {
        boolean exists = cartService.isProductInCart(productId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartItemsCount() {
        int count = cartService.getCartItemsCount();
        return ResponseEntity.ok(Map.of("count", count));
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateCart() {
        cartService.validateCartBeforeCheckout();
        return ResponseEntity.ok(Map.of("status", "valid"));
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/sync-prices")
    public ResponseEntity<Map<String, String>> syncCartPrices() {
        cartService.syncCartWithCurrentPrices();
        return ResponseEntity.ok(Map.of("status", "synced"));
    }
}