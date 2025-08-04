package com.ecommerce.ecommercebackend.dto.response.user;

import com.ecommerce.ecommercebackend.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RegisterResponseDto {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String address;
    private Gender gender;
    private LocalDateTime createdAt;
    private String message;
}
