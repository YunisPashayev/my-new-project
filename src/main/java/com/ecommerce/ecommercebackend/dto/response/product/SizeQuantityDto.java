package com.ecommerce.ecommercebackend.dto.response.product;

import com.ecommerce.ecommercebackend.enums.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeQuantityDto {
    private ProductSize size;    // e.g., "XS", "S"
    private Integer quantity;
}