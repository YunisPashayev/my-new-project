package com.ecommerce.ecommercebackend.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImage;
    private List<String> productImages;
    private String productCategory;
    private String productBrand;
    private String productSku;
    
    private Integer quantity;
    private String size;
    private String color;
    
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    
    private boolean productAvailable;
    private boolean canReview;
    private boolean isReviewed;
    
    private Long storeId;
    private String storeName;
}