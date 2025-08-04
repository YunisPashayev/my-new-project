package com.ecommerce.ecommercebackend.service.impl;

import com.ecommerce.ecommercebackend.dto.request.product.ProductRequestDto;
import com.ecommerce.ecommercebackend.dto.response.product.ProductResponseDto;
import com.ecommerce.ecommercebackend.entity.ProductEntity;
import com.ecommerce.ecommercebackend.entity.ProductImageEntity;
import com.ecommerce.ecommercebackend.entity.ProductSizeQuantity;
import com.ecommerce.ecommercebackend.entity.StoreEntity;
import com.ecommerce.ecommercebackend.enums.ProductStatus;
import com.ecommerce.ecommercebackend.mapper.ProductMapper;
import com.ecommerce.ecommercebackend.repository.ProductRepository;
import com.ecommerce.ecommercebackend.security.util.StoreSecurityUtil;
import com.ecommerce.ecommercebackend.service.ProductService;
import com.ecommerce.ecommercebackend.util.ProductUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final StoreSecurityUtil storeSecurityUtil;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductUtility productUtility;

    @Override
    @Transactional
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) throws AccessDeniedException, java.nio.file.AccessDeniedException {
        StoreEntity store = storeSecurityUtil.getCurrentStore();
        ProductEntity product = productMapper.mapToProductEntity(productRequestDto);
        product.setStore(store);

        List<ProductSizeQuantity> sizeQuantityEntities = productRequestDto.getSizeQuantities()
                .stream()
                .map(dto -> {
                    ProductSizeQuantity entity = new ProductSizeQuantity();
                    entity.setSize(dto.getSize());
                    entity.setQuantity(dto.getQuantity());
                    entity.setProduct(product);
                    return entity;
                })
                .collect(Collectors.toList());

        product.setSizeQuantities(sizeQuantityEntities);

        ProductEntity productToSave = productRepository.save(product);

        List<String> imagePaths = productUtility.saveProductImages(
                productRequestDto.getImageUrls(),
                store.getId(),
                productToSave.getId()
        );

        List<ProductImageEntity> productImages = imagePaths.stream()
                .map(path -> {
                    ProductImageEntity image = new ProductImageEntity();
                    image.setImageUrl(path);
                    image.setProduct(productToSave);
                    return image;
                })
                .collect(Collectors.toList());

        productToSave.setImages(productImages);

        ProductEntity finalProduct = productRepository.save(productToSave);

        return productMapper.mapToProductResponseDto(finalProduct);
    }

    @Override
    @Transactional
    public List<ProductResponseDto> getAllProductsOfCurrentStore() throws AccessDeniedException, java.nio.file.AccessDeniedException {
        StoreEntity store = storeSecurityUtil.getCurrentStore();
        List<ProductEntity> products = productRepository.findByStoreId(store.getId());

        Stream<ProductEntity> stream = products.stream().peek(product -> {
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                product.setImages(List.of(product.getImages().get(0)));
            } else {
                product.setImages(Collections.emptyList());
            }
        });

        return stream
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> getAllActiveProducts() {
        List<ProductEntity> products = productRepository.findByStatus(ProductStatus.ACTIVE);
        return products.stream()
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto getActiveProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new RuntimeException("Product is not available");
        }
        
        return productMapper.mapToProductResponseDto(product);
    }

    @Override
    public List<ProductResponseDto> getActiveProductsByCategory(String category) {
        List<ProductEntity> products = productRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE);
        return products.stream()
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> getActiveProductsByStore(Long storeId) {
        List<ProductEntity> products = productRepository.findByStoreIdAndStatus(storeId, ProductStatus.ACTIVE);
        return products.stream()
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::mapToProductResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
