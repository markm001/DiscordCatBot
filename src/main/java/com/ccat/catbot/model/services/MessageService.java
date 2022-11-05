package com.ccat.catbot.model.services;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public interface MessageService {
    void sendMessageEmbed(Member member, MessageChannel channel, String title, String description, Color color);

    void sendAccessDenied(Member member, MessageChannel channel, String info);
}
