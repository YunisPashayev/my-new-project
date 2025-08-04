package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.OrderEntity;
import com.ecommerce.ecommercebackend.entity.OrderItemEntity;
import com.ecommerce.ecommercebackend.entity.UserEntity;
import com.ecommerce.ecommercebackend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    List<OrderEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
    
    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Page<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    List<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    
    Page<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    
    Optional<OrderEntity> findByOrderNumber(String orderNumber);
    
    Optional<OrderEntity> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<OrderEntity> findByUserIdWithOrderItems(@Param("userId") Long userId);
    
    @Query("SELECT o FROM OrderEntity o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.id = :orderId")
    Optional<OrderEntity> findByIdWithOrderItems(@Param("orderId") Long orderId);
    
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.status = :status")
    Long countByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.user.id = :userId")
    BigDecimal getTotalSpentByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.status = :status")
    BigDecimal getTotalAmountByStatus(@Param("status") OrderStatus status);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.user.id = :userId AND o.status IN :statuses ORDER BY o.createdAt DESC")
    List<OrderEntity> findByUserIdAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<OrderStatus> statuses);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.createdAt BETWEEN :startDate AND :endDate ORDER BY o.createdAt DESC")
    List<OrderEntity> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.estimatedDeliveryDate < :currentDate AND o.status NOT IN ('DELIVERED', 'CANCELLED', 'RETURNED') ORDER BY o.estimatedDeliveryDate ASC")
    List<OrderEntity> findOverdueOrders(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.createdAt >= :date")
    Long countOrdersFromDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT AVG(o.totalAmount) FROM OrderEntity o WHERE o.status = 'DELIVERED'")
    BigDecimal getAverageOrderValue();
    
    @Query("SELECT DISTINCT o FROM OrderEntity o JOIN FETCH o.orderItems oi JOIN FETCH oi.product p WHERE p.store.id = :storeId ORDER BY o.createdAt DESC")
    List<OrderEntity> findByStoreIdWithOrderItems(@Param("storeId") Long storeId);
    
    @Query("SELECT COUNT(DISTINCT o) FROM OrderEntity o JOIN o.orderItems oi JOIN oi.product p WHERE p.store.id = :storeId")
    Long countByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT SUM(oi.totalPrice) FROM OrderItemEntity oi JOIN oi.product p WHERE p.store.id = :storeId AND oi.order.status = 'DELIVERED'")
    BigDecimal getTotalEarningsByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT DISTINCT o FROM OrderEntity o JOIN o.orderItems oi JOIN oi.product p WHERE p.store.id = :storeId AND o.status = :status ORDER BY o.createdAt DESC")
    List<OrderEntity> findByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") OrderStatus status);
}