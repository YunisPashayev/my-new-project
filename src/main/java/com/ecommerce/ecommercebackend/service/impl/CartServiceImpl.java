package com.ecommerce.ecommercebackend.service.impl;

import com.ecommerce.ecommercebackend.dto.request.cart.AddToCartRequestDto;
import com.ecommerce.ecommercebackend.dto.request.cart.UpdateCartItemQuantityRequestDto;
import com.ecommerce.ecommercebackend.dto.response.cart.CartResponseDto;
import com.ecommerce.ecommercebackend.dto.response.cart.CartSummaryDto;
import com.ecommerce.ecommercebackend.entity.CartEntity;
import com.ecommerce.ecommercebackend.entity.ProductEntity;
import com.ecommerce.ecommercebackend.entity.UserEntity;
import com.ecommerce.ecommercebackend.enums.ProductStatus;
import com.ecommerce.ecommercebackend.enums.ProductSize;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import com.ecommerce.ecommercebackend.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;



    @Override
    public CartResponseDto addToCart(AddToCartRequestDto addToCartRequestDto) {
        log.info("Adding product {} to cart", addToCartRequestDto.getProductId());
        
        ProductEntity product = getProductById(addToCartRequestDto.getProductId());
        validateProductAvailability(product);
        validateQuantity(addToCartRequestDto.getQuantity());
        

        UserEntity currentUser = getCurrentUserOrNull();
        
        if (currentUser != null) {

            Optional<CartEntity> existingCartItem = cartRepository.findByUserAndProductAndSize(
                currentUser, product, addToCartRequestDto.getSize()
            );

            CartEntity cartItem;
            if (existingCartItem.isPresent()) {
                cartItem = existingCartItem.get();
                int newQuantity = cartItem.getQuantity() + addToCartRequestDto.getQuantity();
                validateTotalQuantity(newQuantity);
                cartItem.setQuantity(newQuantity);
                log.info("Updated existing cart item quantity to {}", newQuantity);
            } else {
                cartItem = CartEntity.builder()
                    .user(currentUser)
                    .product(product)
                    .quantity(addToCartRequestDto.getQuantity())
                    .size(addToCartRequestDto.getSize())
                    .build();
                log.info("Created new cart item for product {}", product.getId());
            }

            cartItem = cartRepository.save(cartItem);
            return mapToCartResponseDto(cartItem);
        } else {

            log.info("Anonymous user adding product {} to cart", product.getId());
            return createMockCartResponse(product, addToCartRequestDto);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponseDto> getCartItems() {
        UserEntity currentUser = getCurrentUser();
        List<CartEntity> cartItems = cartRepository.findByUserIdWithProductDetails(currentUser.getId());
        
        return cartItems.stream()
            .map(this::mapToCartResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryDto getCartSummary() {
        UserEntity currentUser = getCurrentUser();
        List<CartEntity> cartItems = cartRepository.findByUserIdWithProductDetails(currentUser.getId());
        
        return buildCartSummary(cartItems);
    }

    @Override
    public CartResponseDto updateCartItemQuantity(Long cartItemId, UpdateCartItemQuantityRequestDto requestDto) {
        log.info("Updating cart item {} quantity to {}", cartItemId, requestDto.getQuantity());
        
        CartEntity cartItem = getCartItemById(cartItemId);
        validateCartItemOwnership(cartItem);
        validateQuantity(requestDto.getQuantity());
        validateTotalQuantity(requestDto.getQuantity());
        
        cartItem.setQuantity(requestDto.getQuantity());
        cartItem = cartRepository.save(cartItem);
        
        return mapToCartResponseDto(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCartItem(Long cartItemId) {
        CartEntity cartItem = getCartItemById(cartItemId);
        validateCartItemOwnership(cartItem);
        return mapToCartResponseDto(cartItem);
    }

    @Override
    public void removeFromCart(Long cartItemId) {
        log.info("Removing cart item {}", cartItemId);
        
        CartEntity cartItem = getCartItemById(cartItemId);
        validateCartItemOwnership(cartItem);
        
        cartRepository.delete(cartItem);
    }

    @Override
    public void removeProductFromCart(Long productId) {
        log.info("Removing all cart items for product {}", productId);
        
        UserEntity currentUser = getCurrentUser();
        cartRepository.deleteByUserIdAndProductId(currentUser.getId(), productId);
    }

    @Override
    public void clearCart() {
        log.info("Clearing cart for user");
        
        UserEntity currentUser = getCurrentUser();
        cartRepository.deleteByUserId(currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInCart(Long productId) {
        UserEntity currentUser = getCurrentUser();
        return cartRepository.existsByUserIdAndProductId(currentUser.getId(), productId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemsCount() {
        UserEntity currentUser = getCurrentUser();
        Long count = cartRepository.countByUserId(currentUser.getId());
        return count != null ? count.intValue() : 0;
    }

    @Override
    public CartResponseDto moveToWishlist(Long cartItemId) {

        throw new UnsupportedOperationException("Wishlist functionality not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCartBeforeCheckout() {
        log.info("Validating cart before checkout");
        
        UserEntity currentUser = getCurrentUser();
        List<CartEntity> cartItems = cartRepository.findByUserIdWithProductDetails(currentUser.getId());
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        for (CartEntity cartItem : cartItems) {
            ProductEntity product = cartItem.getProduct();
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new RuntimeException("Product " + product.getName() + " is no longer available");
            }

        }
    }

    @Override
    public void syncCartWithCurrentPrices() {
        log.info("Syncing cart with current prices");
        
        UserEntity currentUser = getCurrentUser();
        List<CartEntity> cartItems = cartRepository.findByUserIdWithProductDetails(currentUser.getId());
        

        cartItems.forEach(cartItem -> {

            ProductEntity product = productRepository.findById(cartItem.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProduct().getId()));
            cartItem.setProduct(product);
        });
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    private UserEntity getCurrentUserOrNull() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(email)) {
                return null;
            }
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    }

    private CartEntity getCartItemById(Long cartItemId) {
        return cartRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found with ID: " + cartItemId));
    }

    private void validateProductAvailability(ProductEntity product) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new RuntimeException("Product is not available: " + product.getName());
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }
        if (quantity > 999) {
            throw new RuntimeException("Quantity cannot exceed 999");
        }
    }

    private void validateTotalQuantity(Integer quantity) {

        validateQuantity(quantity);
    }

    private void validateCartItemOwnership(CartEntity cartItem) {
        UserEntity currentUser = getCurrentUser();
        if (!cartItem.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied to cart item");
        }
    }

    private CartSummaryDto buildCartSummary(List<CartEntity> cartItems) {
        if (cartItems.isEmpty()) {
            return CartSummaryDto.builder()
                .totalItems(0)
                .totalQuantity(0)
                .subtotal(BigDecimal.ZERO)
                .tax(BigDecimal.ZERO)
                .shipping(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .hasOutOfStockItems(false)
                .hasUnavailableItems(false)
                .items(List.of())
                .build();
        }

        List<CartResponseDto> items = cartItems.stream()
            .map(this::mapToCartResponseDto)
            .collect(Collectors.toList());

        int totalQuantity = cartItems.stream()
            .mapToInt(CartEntity::getQuantity)
            .sum();

        BigDecimal subtotal = cartItems.stream()
            .map(item -> BigDecimal.valueOf(item.getProduct().getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasUnavailableItems = cartItems.stream()
            .anyMatch(item -> item.getProduct().getStatus() != ProductStatus.ACTIVE);

        return CartSummaryDto.builder()
            .totalItems(cartItems.size())
            .totalQuantity(totalQuantity)
            .subtotal(subtotal)
            .tax(BigDecimal.ZERO)
            .shipping(BigDecimal.ZERO)
            .discount(BigDecimal.ZERO)
            .total(subtotal)
            .hasOutOfStockItems(false)
            .hasUnavailableItems(hasUnavailableItems)
            .items(items)
            .build();
    }

    private CartResponseDto mapToCartResponseDto(CartEntity cartItem) {
        ProductEntity product = cartItem.getProduct();
        BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartResponseDto.builder()
            .id(cartItem.getId())
            .productId(product.getId())
            .productName(product.getName())
            .productDescription(product.getDescription())
            .productPrice(unitPrice)
            .productImage(product.getImages().isEmpty() ? null : 
                product.getImages().get(0).getImageUrl())
            .productImages(product.getImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList()))
            .productCategory(product.getCategory())
            .quantity(cartItem.getQuantity())
            .size(cartItem.getSize())
            .availableSizes(product.getSizeQuantities().stream()
                .map(sq -> sq.getSize().getLabel())
                .collect(Collectors.toList()))
            .unitPrice(unitPrice)
            .totalPrice(totalPrice)
            .productAvailable(product.getStatus() == ProductStatus.ACTIVE)
            .productStock(999)
            .createdAt(cartItem.getCreatedAt())
            .updatedAt(cartItem.getUpdatedAt())
            .build();
    }
    
    private CartResponseDto createMockCartResponse(ProductEntity product, AddToCartRequestDto requestDto) {
        BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(requestDto.getQuantity()));
        
        return CartResponseDto.builder()
            .id(-1L)
            .productId(product.getId())
            .productName(product.getName())
            .productDescription(product.getDescription())
            .productPrice(unitPrice)
            .productImage(product.getImages().isEmpty() ? null : 
                product.getImages().get(0).getImageUrl())
            .productImages(product.getImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList()))
            .productCategory(product.getCategory())
            .quantity(requestDto.getQuantity())
            .size(requestDto.getSize())
            .availableSizes(product.getSizeQuantities().stream()
                .map(sq -> sq.getSize().getLabel())
                .collect(Collectors.toList()))
            .unitPrice(unitPrice)
            .totalPrice(totalPrice)
            .productAvailable(product.getStatus() == ProductStatus.ACTIVE)
            .productStock(999)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}