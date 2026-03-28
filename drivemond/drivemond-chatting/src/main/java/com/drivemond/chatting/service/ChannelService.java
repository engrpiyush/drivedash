package com.drivemond.chatting.service;

import com.drivemond.chatting.entity.ChannelList;
import com.drivemond.chatting.entity.ChannelUser;
import com.drivemond.chatting.repository.ChannelConversationRepository;
import com.drivemond.chatting.repository.ChannelListRepository;
import com.drivemond.chatting.repository.ChannelUserRepository;
import com.drivemond.core.exception.DrivemondException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelListRepository channelListRepo;
    private final ChannelUserRepository channelUserRepo;
    private final ChannelConversationRepository conversationRepo;

    /**
     * Returns the existing channel for a given trip, or creates one and adds
     * both the customer and driver as members.
     */
    @Transactional
    public ChannelList getOrCreateChannel(UUID tripId, String tripType,
                                          UUID customerId, UUID driverId) {
        return channelListRepo
                .findByChannelableIdAndChannelableType(tripId, tripType)
                .orElseGet(() -> {
                    ChannelList channel = channelListRepo.save(
                            ChannelList.builder()
                                    .channelableId(tripId)
                                    .channelableType(tripType)
                                    .build());
                    channelUserRepo.save(ChannelUser.builder()
                            .channelId(channel.getId()).userId(customerId).build());
                    channelUserRepo.save(ChannelUser.builder()
                            .channelId(channel.getId()).userId(driverId).build());
                    return channel;
                });
    }

    public ChannelList findById(UUID id) {
        return channelListRepo.findById(id)
                .orElseThrow(() -> DrivemondException.notFound("Channel not found"));
    }

    /**
     * Returns all channels the user is a member of, ordered by most-recently
     * updated, with the last message and unread count.
     */
    public Page<ChannelList> getChannelsForUser(UUID userId, int page, int size) {
        List<UUID> channelIds = channelUserRepo.findChannelIdsByUserId(userId);
        if (channelIds.isEmpty()) {
            return Page.empty();
        }

        Page<ChannelList> channels = channelListRepo.findAll(
                (root, q, cb) -> root.get("id").in(channelIds),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt")));

        return channels;
    }

    public List<ChannelUser> getChannelMembers(UUID channelId) {
        return channelUserRepo.findByChannelId(channelId);
    }

    /**
     * Marks all messages in a channel as read for the given user.
     */
    @Transactional
    public void markAsRead(UUID channelId, UUID userId) {
        channelUserRepo.findByChannelIdAndUserId(channelId, userId)
                .ifPresent(cu -> {
                    cu.setRead(true);
                    channelUserRepo.save(cu);
                });
    }

    /**
     * Marks channel as unread for all members except the sender.
     */
    @Transactional
    public void markUnreadForOthers(UUID channelId, UUID senderId) {
        channelUserRepo.findByChannelId(channelId).stream()
                .filter(cu -> !cu.getUserId().equals(senderId))
                .forEach(cu -> {
                    cu.setRead(false);
                    channelUserRepo.save(cu);
                });
    }

    public long getUnreadCount(UUID channelId, UUID userId) {
        return channelUserRepo.countByChannelIdAndIsReadFalseAndUserIdNot(channelId, userId);
    }
}
