package com.ecommerce.ecommercebackend.dto.response.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTrackingDto {
    
    private String trackingNumber;
    private String carrierName;
    private String carrierUrl;
    private List<TrackingEventDto> events;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TrackingEventDto {
    
    private String status;
    private String description;
    private String location;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}