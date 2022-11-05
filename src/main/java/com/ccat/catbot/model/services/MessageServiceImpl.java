package com.ccat.catbot.model.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.OffsetDateTime;

@Service
public class MessageServiceImpl implements MessageService{
    @Override
    public void sendMessageEmbed(Member member, MessageChannel channel, String title, String description, Color color) {
        EmbedBuilder embed = new EmbedBuilder().setColor(color);
        embed.setAuthor(member.getUser().getName());
        embed.setTitle(title);
        embed.setDescription(description);
        embed.setTimestamp(OffsetDateTime.now());

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public void sendAccessDenied(Member member, MessageChannel channel, String info) {
        channel.sendMessage(member.getAsMention() +" "+ info).queue();
    }
}
