package com.ccat.catbot.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="members")
public class GuildMember {
    @Id
    Long id;
    Long memberId;
    Long guildId;
    Long botPermissions;

    public GuildMember() {
    }

    public GuildMember(Long id, Long memberId, Long guildId, Long botPermissions) {
        this.id = id;
        this.memberId = memberId;
        this.guildId = guildId;
        this.botPermissions = botPermissions;
    }

    public GuildMember(Long memberId, Long guildId, Long botPermissions) {
        this.memberId = memberId;
        this.guildId = guildId;
        this.botPermissions = botPermissions;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getGuildId() {
        return guildId;
    }

    public Long getBotPermissions() {
        return botPermissions;
    }
}
