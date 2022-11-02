package com.ccat.catbot.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class MessageServiceImpl implements MessageService{
    @Override
    public void sendMessageEmbed(Member member, MessageChannel channel, String title, String description, Color color) {
        EmbedBuilder embed = new EmbedBuilder().setColor(color);
        embed.setAuthor(member.getEffectiveName());
        embed.setTitle(title);
        embed.setDescription(description);
        embed.setTimestamp(OffsetDateTime.now());

        channel.sendMessageEmbeds(embed.build()).queue();
    }
}
