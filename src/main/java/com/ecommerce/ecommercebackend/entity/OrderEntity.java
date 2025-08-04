package com.ecommerce.ecommercebackend.entity;

import com.ecommerce.ecommercebackend.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders",
       indexes = {
           @Index(name = "idx_order_user", columnList = "user_id"),
           @Index(name = "idx_order_status", columnList = "status"),
           @Index(name = "idx_order_created", columnList = "created_at")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "orderItems"})
@EqualsAndHashCode(exclude = {"user", "orderItems"})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    @NotNull(message = "User is required")
    private UserEntity user;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Order status is required")
    private OrderStatus status;

    @Column(name = "delivery_address", nullable = false, length = 500)
    @NotBlank(message = "Delivery address is required")
    @Size(min = 10, max = 500, message = "Delivery address must be between 10 and 500 characters")
    private String deliveryAddress;

    @Column(name = "phone_number", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Column(length = 1000)
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        if (status == null) {
            status = OrderStatus.PENDING;
        }
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
        calculateEstimatedDelivery();
    }

    @PreUpdate
    private void preUpdate() {
        if (status == OrderStatus.DELIVERED && actualDeliveryDate == null) {
            actualDeliveryDate = LocalDateTime.now();
        }
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    private void calculateEstimatedDelivery() {
        if (estimatedDeliveryDate == null) {
            estimatedDeliveryDate = LocalDateTime.now().plusDays(3);
        }
    }

    public void addOrderItem(OrderItemEntity orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void removeOrderItem(OrderItemEntity orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }

    public BigDecimal calculateTotalAmount() {
        return orderItems.stream()
            .map(OrderItemEntity::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}