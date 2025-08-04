package com.ecommerce.ecommercebackend.dto.request.store;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoreRegisterRequest {

    @NotBlank(message = "Store name is required")
    @Size(min = 3, max = 100, message = "Name must be at least 3 characters")
    private String name;

    @NotBlank(message = "Owner name is required")
    @Size(min = 6, max = 100, message = "Name must be at least 6 characters")
    private String ownerName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(min = 3, max = 150, message = "Email must be at least 3 characters")
    private String email;

    @Size(min = 10, max = 64, message = "Password must be at least 10 characters")
    private String password;

    @Size(min = 10, max = 64, message = "Confirm password must be at least 10 characters")
    private String confirmPassword;

    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Phone number is invalid")
    private String phone;

    private MultipartFile logo;

    private MultipartFile banner;

    @NotBlank(message = "Category is required")
    @Size(min = 3, max = 50, message = "Name must be at least 3 characters")
    private String category;

    @NotBlank(message = "Location is required")
    private String location;

    @AssertTrue(message = "You must agree to the terms")
    private Boolean agreedToTerms;
}
