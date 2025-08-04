package com.ecommerce.ecommercebackend.dto.response.store;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoreResponseDto {
    private Long id;
    private String name;
    private String ownerName;
    private String email;
    private String phone;
    private String logo;
    private String banner;
    private String category;
    private String location;
}
