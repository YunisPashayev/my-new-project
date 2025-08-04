package com.ecommerce.ecommercebackend.repository;

import com.ecommerce.ecommercebackend.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    List<OrderItemEntity> findByOrderId(Long orderId);
}