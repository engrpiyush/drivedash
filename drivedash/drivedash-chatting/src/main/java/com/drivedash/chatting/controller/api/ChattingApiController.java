package com.drivedash.chatting.controller.api;

import com.drivedash.auth.entity.User;
import com.drivedash.chatting.entity.ChannelConversation;
import com.drivedash.chatting.entity.ChannelList;
import com.drivedash.chatting.service.ChannelService;
import com.drivedash.chatting.service.ConversationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST API for chatting — used by the mobile apps for channel management,
 * conversation history, and HTTP-based message sending.
 *
 * <p>Real-time delivery is handled separately via STOMP WebSocket
 * ({@code /ws} endpoint).
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChattingApiController {

    private final ChannelService channelService;
    private final ConversationService conversationService;

    /**
     * List all channels the authenticated user belongs to.
     */
    @GetMapping("/channels")
    public ResponseEntity<Page<ChannelList>> channelList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(channelService.getChannelsForUser(user.getId(), page, size));
    }

    /**
     * Get or create the channel for a trip. Called once when a driver accepts
     * a trip so both parties have a channel ready before chatting starts.
     */
    @PutMapping("/channels")
    public ResponseEntity<ChannelList> getOrCreateChannel(
            @RequestParam UUID tripId,
            @RequestParam(defaultValue = "trip_request") String tripType,
            @RequestParam UUID customerId,
            @RequestParam UUID driverId) {
        return ResponseEntity.ok(
                channelService.getOrCreateChannel(tripId, tripType, customerId, driverId));
    }

    /**
     * Fetch paginated conversation history for a channel (newest first).
     */
    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<Page<ChannelConversation>> conversation(
            @PathVariable UUID channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @AuthenticationPrincipal User user) {
        channelService.markAsRead(channelId, user.getId());
        return ResponseEntity.ok(conversationService.getConversation(channelId, page, size));
    }

    /**
     * Send a message over HTTP (fires STOMP broadcast internally).
     * Supports optional file attachments as multipart.
     */
    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<ChannelConversation> sendMessage(
            @PathVariable UUID channelId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) List<MultipartFile> files,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(
                conversationService.sendMessage(user, channelId, message, files));
    }
}
