package com.ccat.catbot.model.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="serverchannels")
public class ServerChannel {
    @Id
    Long id;

    Long guildId;

    Long channelId;

    @Enumerated(EnumType.STRING)
    ChannelTypeSpecifier specifier;

    public ServerChannel() {
    }

    public ServerChannel(Long id, Long guildId, Long channelId, ChannelTypeSpecifier specifier) {
        this.id = id;
        this.guildId = guildId;
        this.channelId = channelId;
        this.specifier = specifier;
    }

    public ServerChannel(Long guildId, Long channelId, ChannelTypeSpecifier specifier) {
        this.id = UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE;
        this.guildId = guildId;
        this.channelId = channelId;
        this.specifier = specifier;
    }

    public ServerChannel(Long guildId, Long channelId) {
        this.id = UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE;
        this.guildId = guildId;
        this.channelId = channelId;
    }

    public Long getId() {
        return id;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public ChannelTypeSpecifier getSpecifier() {
        return specifier;
    }
}
