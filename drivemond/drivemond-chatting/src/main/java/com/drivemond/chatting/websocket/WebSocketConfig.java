package com.drivemond.chatting.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures the Spring WebSocket + STOMP message broker.
 *
 * <p>Replaces the Laravel-WebSockets / Pusher setup used in the PHP application.
 *
 * <p>Clients (mobile apps) connect to {@code /ws} via SockJS, then subscribe to:
 * <ul>
 *   <li>{@code /topic/chat.{channelId}} — broadcast messages for a channel</li>
 *   <li>{@code /user/queue/notifications} — per-user unread notifications</li>
 * </ul>
 * Messages are sent by clients to {@code /app/chat.send}.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Simple in-memory broker for /topic (broadcast) and /queue (point-to-point)
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix for messages routed to @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific destinations (/user/queue/...)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
