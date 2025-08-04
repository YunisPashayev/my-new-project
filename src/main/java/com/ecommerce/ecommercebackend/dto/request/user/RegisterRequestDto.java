package com.ecommerce.ecommercebackend.dto.request.user;

import com.ecommerce.ecommercebackend.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    private String surname;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(min = 6, message = "Email must be at least 6 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 64, message = "Password must be between 10 and 64 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirm;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    private Gender gender;

    @NotNull(message = "Consent for marketing must be given")
    private Boolean consentMarketing;

    @NotNull(message = "Consent for messages delivered must be given")
    private Boolean consentMessagesDelivered;

    @NotNull(message = "Consent for membership agreement must be given")
    private Boolean consentMembershipAgreement;
}
