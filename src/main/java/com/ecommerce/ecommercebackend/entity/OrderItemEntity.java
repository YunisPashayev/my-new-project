package com.ecommerce.ecommercebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items",
       indexes = {
           @Index(name = "idx_order_item_order", columnList = "order_id"),
           @Index(name = "idx_order_item_product", columnList = "product_id")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"order", "product"})
@EqualsAndHashCode(exclude = {"order", "product"})
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_order"))
    @NotNull(message = "Order is required")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_item_product"))
    @NotNull(message = "Product is required")
    private ProductEntity product;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 999, message = "Quantity cannot exceed 999")
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @Column(length = 10)
    @Size(max = 10, message = "Size cannot exceed 10 characters")
    private String size;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;

    @Column(name = "product_name", nullable = false, length = 255)
    @NotBlank(message = "Product name is required")
    private String productName;

    @Column(name = "product_image", length = 500)
    private String productImage;

    @PrePersist
    @PreUpdate
    private void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void updateFromProduct(ProductEntity product) {
        this.productName = product.getName();
        this.unitPrice = BigDecimal.valueOf(product.getPrice());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            this.productImage = product.getImages().get(0).getImageUrl();
        }
        calculateTotalPrice();
    }
}