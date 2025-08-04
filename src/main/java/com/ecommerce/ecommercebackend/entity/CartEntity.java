package com.ecommerce.ecommercebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id", "size"}),
       indexes = {
           @Index(name = "idx_cart_user", columnList = "user_id"),
           @Index(name = "idx_cart_product", columnList = "product_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "product"})
@EqualsAndHashCode(exclude = {"user", "product"})
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_user"))
    @NotNull(message = "User is required")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cart_product"))
    @NotNull(message = "Product is required")
    private ProductEntity product;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Column(length = 10)
    private String size;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }
    }

    @PreUpdate
    private void preUpdate() {
        if (quantity == null || quantity < 1) {
            quantity = 1;
        }
    }
}