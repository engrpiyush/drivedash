package com.drivemond.chatting.service;

import com.drivemond.auth.entity.User;
import com.drivemond.chatting.dto.ChatMessagePayload;
import com.drivemond.chatting.entity.ChannelConversation;
import com.drivemond.chatting.entity.ConversationFile;
import com.drivemond.chatting.repository.ChannelConversationRepository;
import com.drivemond.chatting.repository.ConversationFileRepository;
import com.drivemond.core.util.FileStorageService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ChannelConversationRepository conversationRepo;
    private final ConversationFileRepository fileRepo;
    private final ChannelService channelService;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;

    public Page<ChannelConversation> getConversation(UUID channelId, int page, int size) {
        return conversationRepo.findByChannelIdOrderByCreatedAtDesc(
                channelId, PageRequest.of(page, size));
    }

    /**
     * Persists the message (and any file attachments), updates unread flags,
     * then broadcasts the payload over STOMP to all channel subscribers.
     */
    @Transactional
    public ChannelConversation sendMessage(User sender, UUID channelId,
                                           String message,
                                           List<MultipartFile> attachments) {
        ChannelConversation conversation = conversationRepo.save(
                ChannelConversation.builder()
                        .channelId(channelId)
                        .userId(sender.getId())
                        .message(message)
                        .build());

        List<ChatMessagePayload.FileDto> fileDtos = new ArrayList<>();
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    String stored = fileStorageService.store(file, "conversation");
                    String fileType = file.getContentType() != null
                            ? file.getContentType() : "application/octet-stream";
                    ConversationFile cf = fileRepo.save(ConversationFile.builder()
                            .conversationId(conversation.getId())
                            .fileName(stored)
                            .fileType(fileType)
                            .build());
                    fileDtos.add(ChatMessagePayload.FileDto.builder()
                            .id(cf.getId())
                            .fileName(cf.getFileName())
                            .fileType(cf.getFileType())
                            .build());
                }
            }
        }

        channelService.markUnreadForOthers(channelId, sender.getId());

        ChatMessagePayload payload = ChatMessagePayload.builder()
                .conversationId(conversation.getId())
                .channelId(channelId)
                .senderId(sender.getId())
                .senderName(sender.getFirstName() + " "
                        + (sender.getLastName() != null ? sender.getLastName() : ""))
                .message(message)
                .files(fileDtos)
                .createdAt(conversation.getCreatedAt())
                .build();

        messagingTemplate.convertAndSend("/topic/chat." + channelId, payload);

        return conversation;
    }
}
