package com.fooddelivery.service;

import com.fooddelivery.dto.OrderRequest;
import com.fooddelivery.dto.OrderResponse;
import com.fooddelivery.model.Order;
import com.fooddelivery.model.OrderItem;
import com.fooddelivery.model.OrderStatus;
import com.fooddelivery.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final WebSocketNotificationService webSocketService;
    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());
        
        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> OrderItem.builder()
                        .itemName(itemRequest.getItemName())
                        .quantity(itemRequest.getQuantity())
                        .price(itemRequest.getPrice())
                        .specialInstructions(itemRequest.getSpecialInstructions())
                        .build())
                .collect(Collectors.toList());
        
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .restaurantId(request.getRestaurantId())
                .totalAmount(request.getTotalAmount())
                .deliveryAddress(request.getDeliveryAddress())
                .customerPhone(request.getCustomerPhone())
                .status(OrderStatus.PENDING)
                .items(items)
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();
        
        order = orderRepository.save(order);
        
        // Send real-time notification
        webSocketService.notifyOrderCreated(order);
        
        log.info("Order created successfully with ID: {}", order.getId());
        return mapToResponse(order);
    }
    
    @Cacheable(value = "orders", key = "#id")
    public OrderResponse getOrderById(UUID id) {
        log.info("Fetching order with ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return mapToResponse(order);
    }
    
    @Cacheable(value = "restaurantOrders", key = "#restaurantId")
    public List<OrderResponse> getOrdersByRestaurant(UUID restaurantId) {
        log.info("Fetching orders for restaurant: {}", restaurantId);
        return orderRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "customerOrders", key = "#customerId")
    public List<OrderResponse> getOrdersByCustomer(UUID customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    @CacheEvict(value = {"orders", "restaurantOrders", "customerOrders"}, allEntries = true)
    public OrderResponse updateOrderStatus(UUID id, OrderStatus status) {
        log.info("Updating order {} status to {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        order.setStatus(status);
        
        if (status == OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
        }
        
        order = orderRepository.save(order);
        
        // Send real-time notification
        webSocketService.notifyOrderStatusUpdate(order);
        
        return mapToResponse(order);
    }
    
    @Transactional
    @CacheEvict(value = {"orders", "restaurantOrders", "customerOrders"}, allEntries = true)
    public OrderResponse assignDriver(UUID orderId, UUID driverId) {
        log.info("Assigning driver {} to order {}", driverId, orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        order.setDriverId(driverId);
        order.setStatus(OrderStatus.ACCEPTED);
        
        order = orderRepository.save(order);
        
        // Send real-time notification
        webSocketService.notifyDriverAssigned(order);
        
        return mapToResponse(order);
    }
    
    public List<OrderResponse> getPendingOrders() {
        return orderRepository.findPendingOrdersByStatus(OrderStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private OrderResponse mapToResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .specialInstructions(item.getSpecialInstructions())
                        .build())
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .restaurantId(order.getRestaurantId())
                .driverId(order.getDriverId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .customerPhone(order.getCustomerPhone())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .deliveredAt(order.getDeliveredAt())
                .build();
    }
}
