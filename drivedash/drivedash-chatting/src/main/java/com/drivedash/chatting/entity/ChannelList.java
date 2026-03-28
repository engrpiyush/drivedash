package com.drivedash.chatting.entity;

import com.drivedash.core.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channel_lists")
@SQLDelete(sql = "UPDATE channel_lists SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class ChannelList extends BaseEntity {

    /**
     * The ID of the entity this channel is attached to (e.g. a trip_request UUID).
     */
    @Column(name = "channelable_id", columnDefinition = "CHAR(36)")
    private UUID channelableId;

    /**
     * Discriminator for the owning entity type (e.g. "trip_request", "parcel").
     */
    @Column(name = "channelable_type", length = 50)
    private String channelableType;
}
