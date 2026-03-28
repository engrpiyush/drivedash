package com.drivemond.chatting.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * STOMP broadcast payload sent to /topic/chat.{channelId} after each new message.
 */
@Getter
@Builder
public class ChatMessagePayload {

    private UUID conversationId;
    private UUID channelId;
    private UUID senderId;
    private String senderName;
    private String message;
    private List<FileDto> files;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class FileDto {
        private UUID id;
        private String fileName;
        private String fileType;
    }
}
