package com.fooddelivery.service;

import com.fooddelivery.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {
    
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    public void addSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
        log.info("Session added: {}", sessionId);
    }
    
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        log.info("Session removed: {}", sessionId);
    }
    
    public void notifyOrderCreated(Order order) {
        String message = String.format("{\"type\":\"ORDER_CREATED\",\"data\":%s}", toJson(order));
        broadcastToRestaurant(order.getRestaurantId().toString(), message);
        broadcastToCustomer(order.getCustomerId().toString(), message);
    }
    
    public void notifyOrderStatusUpdate(Order order) {
        String message = String.format("{\"type\":\"ORDER_STATUS_UPDATE\",\"data\":%s}", toJson(order));
        broadcastToRestaurant(order.getRestaurantId().toString(), message);
        broadcastToCustomer(order.getCustomerId().toString(), message);
        if (order.getDriverId() != null) {
            broadcastToDriver(order.getDriverId().toString(), message);
        }
    }
    
    public void notifyDriverAssigned(Order order) {
        String message = String.format("{\"type\":\"DRIVER_ASSIGNED\",\"data\":%s}", toJson(order));
        broadcastToDriver(order.getDriverId().toString(), message);
    }
    
    private void broadcastToRestaurant(String restaurantId, String message) {
        broadcastToTopic("/topic/restaurant/" + restaurantId, message);
    }
    
    private void broadcastToCustomer(String customerId, String message) {
        broadcastToTopic("/topic/customer/" + customerId, message);
    }
    
    private void broadcastToDriver(String driverId, String message) {
        broadcastToTopic("/topic/driver/" + driverId, message);
    }
    
    private void broadcastToTopic(String topic, String message) {
        log.info("Broadcasting to {}: {}", topic, message);
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                log.error("Error sending message to session", e);
            }
        });
    }
    
    private String toJson(Order order) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "id", order.getId(),
                    "customerId", order.getCustomerId(),
                    "restaurantId", order.getRestaurantId(),
                    "driverId", order.getDriverId(),
                    "status", order.getStatus(),
                    "totalAmount", order.getTotalAmount(),
                    "deliveryAddress", order.getDeliveryAddress(),
                    "createdAt", order.getCreatedAt()
            ));
        } catch (Exception e) {
            log.error("Error converting order to JSON", e);
            return "{}";
        }
    }
}
