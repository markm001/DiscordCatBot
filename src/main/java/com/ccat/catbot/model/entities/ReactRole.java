package com.ccat.catbot.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="reactroles")
public class ReactRole {
    @Id
    private Long id;
    private Long guildId;
    private Long channelId;
    private Long messageId;
    private Long roleId;

    //TODO: Separate into different table & JOIN FETCH
    private String emote;

    public ReactRole() {

    }

    public ReactRole(Long id, Long guildId, Long channelId, Long messageId, Long roleId, String emote) {
        this.id = id;
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.roleId = roleId;
        this.emote = emote;
    }

    public ReactRole(Long guildId, Long channelId, Long messageId, String emote) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.emote = emote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuildId() {
        return guildId;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getEmote() {
        return emote;
    }

    public void setEmote(String emote) {
        this.emote = emote;
    }
}
