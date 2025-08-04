package com.ecommerce.ecommercebackend.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Pending", "Order has been placed and awaiting confirmation", true),
    CONFIRMED("Confirmed", "Order has been confirmed and is being prepared", true),
    PROCESSING("Processing", "Order is being processed and packaged", true),
    SHIPPED("Shipped", "Order has been shipped and is on the way", true),
    OUT_FOR_DELIVERY("Out for Delivery", "Order is out for delivery", true),
    DELIVERED("Delivered", "Order has been successfully delivered", false),
    CANCELLED("Cancelled", "Order has been cancelled", false),
    RETURNED("Returned", "Order has been returned by customer", false),
    REFUNDED("Refunded", "Order has been refunded", false);

    private final String displayName;
    private final String description;
    private final boolean canBeModified;

    OrderStatus(String displayName, String description, boolean canBeModified) {
        this.displayName = displayName;
        this.description = description;
        this.canBeModified = canBeModified;
    }

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == PROCESSING || this == SHIPPED || this == OUT_FOR_DELIVERY;
    }

    public boolean isCompleted() {
        return this == DELIVERED;
    }

    public boolean isCancelled() {
        return this == CANCELLED || this == RETURNED || this == REFUNDED;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED:
                return newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED:
                return newStatus == OUT_FOR_DELIVERY || newStatus == DELIVERED;
            case OUT_FOR_DELIVERY:
                return newStatus == DELIVERED;
            case DELIVERED:
                return newStatus == RETURNED;
            case RETURNED:
                return newStatus == REFUNDED;
            default:
                return false;
        }
    }
}