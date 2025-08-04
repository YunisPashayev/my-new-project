package com.ecommerce.ecommercebackend.service;

import com.ecommerce.ecommercebackend.dto.request.cart.AddToCartRequestDto;
import com.ecommerce.ecommercebackend.dto.request.cart.UpdateCartItemQuantityRequestDto;
import com.ecommerce.ecommercebackend.dto.response.cart.CartResponseDto;
import com.ecommerce.ecommercebackend.dto.response.cart.CartSummaryDto;

import java.util.List;

public interface CartService {
    
    CartResponseDto addToCart(AddToCartRequestDto addToCartRequestDto);
    
    List<CartResponseDto> getCartItems();
    
    CartSummaryDto getCartSummary();
    
    CartResponseDto updateCartItemQuantity(Long cartItemId, UpdateCartItemQuantityRequestDto requestDto);
    
    CartResponseDto getCartItem(Long cartItemId);
    
    void removeFromCart(Long cartItemId);
    
    void removeProductFromCart(Long productId);
    
    void clearCart();
    
    boolean isProductInCart(Long productId);
    
    int getCartItemsCount();
    
    CartResponseDto moveToWishlist(Long cartItemId);
    
    void validateCartBeforeCheckout();
    
    void syncCartWithCurrentPrices();
}