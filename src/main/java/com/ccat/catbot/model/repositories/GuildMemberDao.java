package com.ccat.catbot.model.repositories;

import com.ccat.catbot.model.entities.GuildMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GuildMemberDao extends JpaRepository<GuildMember, Long> {
    @Query("SELECT m FROM GuildMember m WHERE guildId=:guildId AND memberId=:memberId AND botPermissions=:permissionCode")
    Optional<GuildMember> findMemberPermissions(Long guildId, Long memberId, Long permissionCode);
}
