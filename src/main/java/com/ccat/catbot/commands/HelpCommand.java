package com.ccat.catbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.time.OffsetDateTime;

public class HelpCommand implements ServerCommand{
    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        message.delete().queue();
        EmbedBuilder embed = new EmbedBuilder();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("!help - to receive a list of commands. \n");
        stringBuilder.append("!ping - to receive a pong from the bot. \n");

        embed.setTitle("List of available Commands for this bot:");
        embed.setColor(0xfcbe03);
        embed.setDescription(stringBuilder);
        embed.setTimestamp(OffsetDateTime.now());

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessageEmbeds(embed.build()).queue();
        });
    }
}
