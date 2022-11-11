package com.ccat.catbot.model.services;

import com.ccat.catbot.model.entities.ServerChannel;
import com.ccat.catbot.model.repositories.ServerChannelDao;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ServerChannelService {
    private final ServerChannelDao serverChannelDao;

    public ServerChannelService(ServerChannelDao serverChannelDao) {
        this.serverChannelDao = serverChannelDao;
    }

    public ServerChannel setServerChannel(ServerChannel request) {
        ServerChannel serverChannelRequest = new ServerChannel(
                request.getId(),
                request.getGuildId(),
                request.getChannelId(),
                request.getSpecifier());

        serverChannelDao.save(serverChannelRequest);
        return serverChannelRequest;
    }

    public Optional<ServerChannel> getServerChannelFromGuildAndChannel(ServerChannel request) {
        return serverChannelDao.findByGuildAndChannelId(request.getGuildId(), request.getChannelId());
    }

    public boolean checkServerChannelForSpecifier(ServerChannel request) {
        Optional<ServerChannel> channelResponse = serverChannelDao.findExactServerChannel(request.getGuildId(), request.getChannelId(), request.getSpecifier());
        if(channelResponse.isPresent()) {
            return true;
        }
        return false;
    }
}
