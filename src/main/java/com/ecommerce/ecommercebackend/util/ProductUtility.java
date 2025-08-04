package com.ecommerce.ecommercebackend.util;

import com.ecommerce.ecommercebackend.cloudinary.CloudinaryService;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProductUtility {

    private final CloudinaryService cloudinaryService;

    public List<String> saveProductImages(List<MultipartFile> images, Long storeId, Long productId) {
        List<String> imageUrls = new ArrayList<>();
        String folderPath = "image/store_" + storeId + "/product_" + productId;

        for (int i = 0; i < images.size(); i++) {
            MultipartFile image = images.get(i);
            String publicId = "product_" + i;
            String imageUrl = cloudinaryService.uploadFile(image, folderPath, publicId);
            imageUrls.add(imageUrl);
        }

        return imageUrls;
    }
}

