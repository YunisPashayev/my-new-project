package com.ecommerce.ecommercebackend.dto.response.cart;

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
public class CartSummaryDto {
    
    private Integer totalItems;
    private Integer totalQuantity;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal discount;
    private BigDecimal total;
    private boolean hasOutOfStockItems;
    private boolean hasUnavailableItems;
    private List<CartResponseDto> items;
}