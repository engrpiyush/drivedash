package com.drivedash.chatting.repository;

import com.drivedash.chatting.entity.ConversationFile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationFileRepository extends JpaRepository<ConversationFile, UUID> {

    List<ConversationFile> findByConversationId(UUID conversationId);
}
