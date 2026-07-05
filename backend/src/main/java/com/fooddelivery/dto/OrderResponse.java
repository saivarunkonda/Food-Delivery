package com.fooddelivery.dto;

import com.fooddelivery.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private UUID driverId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String customerPhone;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime deliveredAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private String itemName;
        private Integer quantity;
        private BigDecimal price;
        private String specialInstructions;
    }
}
