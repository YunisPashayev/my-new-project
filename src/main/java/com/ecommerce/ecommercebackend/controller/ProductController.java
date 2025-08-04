package com.ecommerce.ecommercebackend.controller;

import com.ecommerce.ecommercebackend.dto.request.product.ProductRequestDto;
import com.ecommerce.ecommercebackend.dto.response.product.ProductResponseDto;
import com.ecommerce.ecommercebackend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductResponseDto> addProduct(@ModelAttribute @Valid ProductRequestDto productRequestDto)
            throws AccessDeniedException, java.nio.file.AccessDeniedException {
        ProductResponseDto savedProduct = productService.addProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/my-store")
    public ResponseEntity<List<ProductResponseDto>> getProductsOfCurrentStore() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        List<ProductResponseDto> products = productService.getAllProductsOfCurrentStore();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProductResponseDto>> getAllActiveProducts() {
        List<ProductResponseDto> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getActiveProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/public/category/{category}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponseDto> products = productService.getActiveProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/public/store/{storeId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByStore(@PathVariable Long storeId) {
        List<ProductResponseDto> products = productService.getActiveProductsByStore(storeId);
        return ResponseEntity.ok(products);
    }
}