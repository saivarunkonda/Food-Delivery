package com.fooddelivery.config;

import com.fooddelivery.service.WebSocketNotificationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final WebSocketNotificationService notificationService;
    
    public WebSocketConfig(WebSocketNotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new OrderWebSocketHandler(notificationService), "/ws/orders")
                .setAllowedOrigins("*");
    }
}
