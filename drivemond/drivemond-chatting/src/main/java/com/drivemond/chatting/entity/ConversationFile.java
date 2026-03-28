package com.drivemond.chatting.entity;

import com.drivemond.core.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation_files")
public class ConversationFile extends BaseAuditEntity {

    @Column(name = "conversation_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID conversationId;

    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;
}
