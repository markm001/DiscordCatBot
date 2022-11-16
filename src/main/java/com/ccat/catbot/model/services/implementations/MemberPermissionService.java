package com.ccat.catbot.model.services.implementations;

import com.ccat.catbot.model.entities.GuildMember;
import com.ccat.catbot.model.repositories.GuildMemberDao;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MemberPermissionService {
    private final GuildMemberDao memberDao;

    public MemberPermissionService(GuildMemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public GuildMember grantMemberPermission(GuildMember request) {
        GuildMember guildMemberRequest = new GuildMember(
                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                request.getMemberId(),
                request.getGuildId(),
                request.getBotPermissions()
        );

        memberDao.save(guildMemberRequest);
        return guildMemberRequest;
    }

    public boolean checkMemberPermissions(GuildMember guildMemberRequest) {
        Optional<GuildMember> guildMemberResponse = memberDao.findMemberPermissions(
                guildMemberRequest.getGuildId(),
                guildMemberRequest.getMemberId(),
                guildMemberRequest.getBotPermissions()
        );
        return guildMemberResponse.isPresent();
    }
}
