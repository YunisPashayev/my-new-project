package com.ecommerce.ecommercebackend.service.impl;

import com.ecommerce.ecommercebackend.dto.request.order.CreateOrderRequestDto;
import com.ecommerce.ecommercebackend.dto.response.order.OrderItemResponseDto;
import com.ecommerce.ecommercebackend.dto.response.order.OrderResponseDto;
import com.ecommerce.ecommercebackend.entity.*;
import com.ecommerce.ecommercebackend.enums.OrderStatus;
import com.ecommerce.ecommercebackend.repository.CartRepository;
import com.ecommerce.ecommercebackend.repository.OrderRepository;
import com.ecommerce.ecommercebackend.repository.UserRepository;
import com.ecommerce.ecommercebackend.security.util.StoreSecurityUtil;
import com.ecommerce.ecommercebackend.service.OrderService;
import org.springframework.security.access.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final StoreSecurityUtil storeSecurityUtil;

    @Override
    public OrderResponseDto createOrderFromCart(CreateOrderRequestDto createOrderRequestDto) {
        log.info("Creating order from cart for user");
        
        UserEntity currentUser = getCurrentUser();
        List<CartEntity> cartItems = cartRepository.findByUserIdWithProductDetails(currentUser.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        BigDecimal totalAmount = cartItems.stream()
            .map(item -> BigDecimal.valueOf(item.getProduct().getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = OrderEntity.builder()
            .user(currentUser)
            .totalAmount(totalAmount)
            .status(OrderStatus.PENDING)
            .deliveryAddress(createOrderRequestDto.getDeliveryAddress())
            .phoneNumber(createOrderRequestDto.getPhoneNumber())
            .notes(createOrderRequestDto.getNotes())
            .build();


        final OrderEntity savedOrder = orderRepository.save(order);

        List<OrderItemEntity> orderItems = cartItems.stream()
            .map(cartItem -> {
                OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(savedOrder)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .size(cartItem.getSize())
                    .unitPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()))
                    .totalPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()).multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
                orderItem.updateFromProduct(cartItem.getProduct());
                return orderItem;
            })
            .collect(Collectors.toList());

        savedOrder.setOrderItems(orderItems);
        order = orderRepository.save(savedOrder);

        cartRepository.deleteByUserId(currentUser.getId());

        log.info("Order created successfully with ID: {}", order.getId());
        return mapToOrderResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders() {
        UserEntity currentUser = getCurrentUser();
        List<OrderEntity> orders = orderRepository.findByUserIdWithOrderItems(currentUser.getId());
        return orders.stream()
            .map(this::mapToOrderResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        UserEntity currentUser = getCurrentUser();
        OrderEntity order = orderRepository.findByIdWithOrderItems(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToOrderResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream()
            .map(this::mapToOrderResponseDto)
            .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, String status) {
        log.info("Updating order {} status to {}", orderId, status);
        
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            
            if (!order.getStatus().canTransitionTo(newStatus)) {
                throw new RuntimeException("Cannot change status from " + order.getStatus() + " to " + newStatus);
            }
            
            order.setStatus(newStatus);
            order = orderRepository.save(order);
            return mapToOrderResponseDto(order);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    @Override
    public void cancelOrder(Long orderId) {
        log.info("Cancelling order {}", orderId);
        
        UserEntity currentUser = getCurrentUser();
        OrderEntity order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private UserEntity getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private OrderResponseDto mapToOrderResponseDto(OrderEntity order) {
        return OrderResponseDto.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .status(order.getStatus())
            .statusDisplayName(order.getStatus().getDisplayName())
            .statusDescription(order.getStatus().getDescription())
            .canBeModified(order.getStatus().isCanBeModified())
            .subtotal(order.getTotalAmount())
            .tax(BigDecimal.ZERO)
            .shipping(BigDecimal.ZERO)
            .discount(BigDecimal.ZERO)
            .totalAmount(order.getTotalAmount())
            .deliveryAddress(order.getDeliveryAddress())
            .phoneNumber(order.getPhoneNumber())
            .notes(order.getNotes())
            .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
            .actualDeliveryDate(order.getActualDeliveryDate())
            .totalItems(order.getOrderItems() != null ? order.getOrderItems().size() : 0)
            .totalQuantity(order.getOrderItems() != null ? 
                order.getOrderItems().stream().mapToInt(OrderItemEntity::getQuantity).sum() : 0)
            .orderItems(order.getOrderItems() != null ? 
                order.getOrderItems().stream().map(this::mapToOrderItemResponseDto).collect(Collectors.toList()) : List.of())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .build();
    }

    private OrderItemResponseDto mapToOrderItemResponseDto(OrderItemEntity orderItem) {
        return OrderItemResponseDto.builder()
            .id(orderItem.getId())
            .productId(orderItem.getProduct().getId())
            .productName(orderItem.getProductName())
            .productImage(orderItem.getProductImage())
            .productCategory(orderItem.getProduct().getCategory())
            .quantity(orderItem.getQuantity())
            .size(orderItem.getSize())
            .unitPrice(orderItem.getUnitPrice())
            .totalPrice(orderItem.getTotalPrice())
            .productAvailable(orderItem.getProduct().getStatus().name().equals("ACTIVE"))
            .build();
    }

    @Override
    public List<OrderResponseDto> getStoreOrders() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        StoreEntity store = storeSecurityUtil.getCurrentStore();
        List<OrderEntity> orders = orderRepository.findByStoreIdWithOrderItems(store.getId());
        
        return orders.stream()
                .map(this::mapToOrderResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getStoreOrderCount() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        StoreEntity store = storeSecurityUtil.getCurrentStore();
        return orderRepository.countByStoreId(store.getId());
    }

    @Override
    public BigDecimal getStoreEarnings() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        StoreEntity store = storeSecurityUtil.getCurrentStore();
        BigDecimal earnings = orderRepository.getTotalEarningsByStoreId(store.getId());
        return earnings != null ? earnings : BigDecimal.ZERO;
    }
}