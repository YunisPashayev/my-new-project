package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.CartEntity;
import com.ecommerce.ecommercebackend.entity.ProductEntity;
import com.ecommerce.ecommercebackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    
    List<CartEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
    
    List<CartEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<CartEntity> findByUserAndProductAndSize(UserEntity user, ProductEntity product, String size);
    
    Optional<CartEntity> findByUserIdAndProductIdAndSize(Long userId, Long productId, String size);
    
    @Query("SELECT COUNT(c) FROM CartEntity c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.quantity) FROM CartEntity c WHERE c.user.id = :userId")
    Long getTotalQuantityByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.quantity * c.product.price) FROM CartEntity c WHERE c.user.id = :userId")
    BigDecimal getTotalAmountByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM CartEntity c JOIN FETCH c.product p LEFT JOIN FETCH p.images WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<CartEntity> findByUserIdWithProductDetails(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    void deleteByUser(UserEntity user);
    
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartEntity c WHERE c.user.id = :userId AND c.product.id = :productId")
    void deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    boolean existsByUserAndProduct(UserEntity user, ProductEntity product);
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}