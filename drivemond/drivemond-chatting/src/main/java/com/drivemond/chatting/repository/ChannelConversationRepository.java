package com.drivemond.chatting.repository;

import com.drivemond.chatting.entity.ChannelConversation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelConversationRepository extends JpaRepository<ChannelConversation, UUID> {

    Page<ChannelConversation> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    @Query("SELECT c FROM ChannelConversation c WHERE c.channelId = :channelId " +
           "ORDER BY c.createdAt DESC LIMIT 1")
    Optional<ChannelConversation> findLastByChannelId(@Param("channelId") UUID channelId);
}
