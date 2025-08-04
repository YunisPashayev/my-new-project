package com.ecommerce.ecommercebackend.mapper;

import com.ecommerce.ecommercebackend.dto.request.store.StoreRegisterRequest;
import com.ecommerce.ecommercebackend.dto.response.store.StoreResponseDto;
import com.ecommerce.ecommercebackend.entity.StoreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productEntities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "logo", ignore = true)
    @Mapping(target = "banner", ignore = true)
    StoreEntity mapToStoreEntity(StoreRegisterRequest storeRegisterRequest);


//    @Mapping(target = "logo", expression = "java(mapImage(storeEntity.getId(), \"logo\", storeEntity.getLogo()))")
//    @Mapping(target = "banner", expression = "java(mapImage(storeEntity.getId(), \"banner\", storeEntity.getBanner()))")
    StoreResponseDto mapToStoreResponse(StoreEntity storeEntity);

//    default String mapImage(Long storeId, String type, String filename) {
//        return filename != null && !filename.isEmpty()
//                ? "images/store_" + storeId + "/" + type + "/" + filename
//                : null;
//    }
}
