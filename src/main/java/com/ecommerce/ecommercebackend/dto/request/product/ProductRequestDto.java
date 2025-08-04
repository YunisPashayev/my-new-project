package com.ecommerce.ecommercebackend.dto.request.product;

import com.ecommerce.ecommercebackend.dto.response.product.SizeQuantityDto;
import com.ecommerce.ecommercebackend.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Data
@NoArgsConstructor
public class ProductRequestDto {
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Double price;

    @NotBlank(message = "Category is required")
    @Size(max = 30, message = "Category must be less than 30 characters")
    private String category;

    @NotBlank(message = "Colors field is required (comma-separated values)")
    private String colors;

    private ProductStatus status;

    @NotEmpty(message = "Sizes with quantities are required")
    private List<SizeQuantityDto> sizeQuantities;

    @NotEmpty(message = "At least one product image must be uploaded")
    private List<@NotNull MultipartFile> imageUrls;

}

