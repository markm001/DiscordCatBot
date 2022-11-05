package com.ccat.catbot.model.services;

import com.ccat.catbot.model.entities.ReactRole;
import com.ccat.catbot.model.repositories.ReactRoleDao;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReactRoleService {
    private final ReactRoleDao reactRoleDao;

    public ReactRoleService(ReactRoleDao reactRoleDao) {
        this.reactRoleDao = reactRoleDao;
    }

    public ReactRole createReactRole(ReactRole request) {
        ReactRole reactRoleResponse = new ReactRole(
                UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE,
                request.getGuildId(),
                request.getChannelId(),
                request.getMessageId(),
                request.getRoleId(),
                request.getEmote()
        );
        reactRoleDao.save(reactRoleResponse);
        return reactRoleResponse;
    }

    public Optional<ReactRole> findReactRole(ReactRole request) {
        ReactRole reactRole = new ReactRole(
                UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE,
                request.getGuildId(),
                request.getChannelId(),
                request.getMessageId(),
                request.getRoleId(),
                request.getEmote()
        );
        return reactRoleDao.findExact(reactRole.getGuildId(),reactRole.getChannelId(), reactRole.getMessageId(), reactRole.getRoleId(), reactRole.getEmote());
    }
}
