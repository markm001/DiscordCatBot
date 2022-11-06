package com.ccat.catbot.model.repositories;

import com.ccat.catbot.model.entities.ReactRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReactRoleDao extends JpaRepository<ReactRole, Long> {
    @Query("SELECT r FROM ReactRole r WHERE guildId=:guildId AND channelId=:channelId AND messageId=:messageId AND emote LIKE :emote")
    Optional<ReactRole> findExact(Long guildId, Long channelId, Long messageId, String emote);

}
