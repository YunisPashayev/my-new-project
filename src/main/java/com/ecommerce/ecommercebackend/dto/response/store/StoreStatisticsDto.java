package com.ecommerce.ecommercebackend.dto.response.store;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStatisticsDto {
    private Long totalOrders;
    private BigDecimal totalEarnings;
    private Long totalProducts;
}