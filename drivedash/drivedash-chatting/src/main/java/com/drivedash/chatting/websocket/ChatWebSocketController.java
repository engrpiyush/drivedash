package com.drivedash.chatting.websocket;

import com.drivedash.auth.entity.User;
import com.drivedash.auth.repository.UserRepository;
import com.drivedash.chatting.dto.SendMessageRequest;
import com.drivedash.chatting.service.ConversationService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * Handles STOMP messages sent by clients to {@code /app/chat.send}.
 *
 * <p>The authenticated user is identified via the STOMP connection's
 * {@link Principal}, which Spring Security populates from the JWT supplied
 * during the WebSocket HTTP upgrade handshake.
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload @Valid SendMessageRequest req,
                              Principal principal) {
        User sender = userRepository.findByEmail(principal.getName())
                .or(() -> userRepository.findByPhone(principal.getName()))
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        conversationService.sendMessage(sender, req.getChannelId(),
                req.getMessage(), Collections.emptyList());
    }
}
