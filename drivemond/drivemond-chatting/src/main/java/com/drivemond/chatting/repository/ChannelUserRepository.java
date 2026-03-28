package com.drivemond.chatting.repository;

import com.drivemond.chatting.entity.ChannelUser;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelUserRepository extends JpaRepository<ChannelUser, UUID> {

    List<ChannelUser> findByChannelId(UUID channelId);

    Optional<ChannelUser> findByChannelIdAndUserId(UUID channelId, UUID userId);

    /**
     * All channel IDs this user belongs to (used to load user's channel list).
     */
    @Query("SELECT cu.channelId FROM ChannelUser cu WHERE cu.userId = :userId")
    List<UUID> findChannelIdsByUserId(@Param("userId") UUID userId);

    long countByChannelIdAndIsReadFalseAndUserIdNot(UUID channelId, UUID userId);
}
