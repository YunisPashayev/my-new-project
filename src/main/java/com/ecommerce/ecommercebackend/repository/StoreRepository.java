package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findStoreEntitiesByEmail(String email);

    boolean existsByEmail(String email);
}