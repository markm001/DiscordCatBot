package com.ccat.catbot.model.repositories;

import com.ccat.catbot.model.entities.ChannelTypeSpecifier;
import com.ccat.catbot.model.entities.ServerChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServerChannelDao extends JpaRepository<ServerChannel, Long> {
    @Query("SELECT s FROM ServerChannel s WHERE guildId=:guildId AND channelId=:channelId")
    Optional<ServerChannel> findByGuildAndChannelId(Long guildId, Long channelId);

    @Query("SELECT s FROM ServerChannel s WHERE guildId=:guildId AND channelId=:channelId AND specifier LIKE :specifier")
    Optional<ServerChannel> findExactServerChannel(Long guildId, Long channelId, ChannelTypeSpecifier specifier);
}
