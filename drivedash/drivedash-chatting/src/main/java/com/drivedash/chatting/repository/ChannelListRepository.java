package com.drivedash.chatting.repository;

import com.drivedash.chatting.entity.ChannelList;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelListRepository extends JpaRepository<ChannelList, UUID>,
        JpaSpecificationExecutor<ChannelList> {

    Optional<ChannelList> findByChannelableIdAndChannelableType(UUID channelableId,
                                                                 String channelableType);
}
