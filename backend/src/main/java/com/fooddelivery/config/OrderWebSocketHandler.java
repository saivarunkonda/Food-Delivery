package com.fooddelivery.config;

import com.fooddelivery.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class OrderWebSocketHandler extends TextWebSocketHandler {
    
    private final WebSocketNotificationService notificationService;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = UUID.randomUUID().toString();
        session.getAttributes().put("sessionId", sessionId);
        notificationService.addSession(sessionId, session);
        log.info("WebSocket connection established: {}", sessionId);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: {}", payload);
        // Handle incoming messages if needed
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = (String) session.getAttributes().get("sessionId");
        notificationService.removeSession(sessionId);
        log.info("WebSocket connection closed: {}", sessionId);
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error", exception);
        String sessionId = (String) session.getAttributes().get("sessionId");
        notificationService.removeSession(sessionId);
    }
}
