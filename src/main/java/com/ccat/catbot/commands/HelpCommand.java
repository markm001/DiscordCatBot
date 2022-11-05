package com.ccat.catbot.commands;

import com.ccat.catbot.model.services.MessageService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class HelpCommand implements ServerCommand{
    private MessageService messageService;

    public HelpCommand(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        message.delete().queue();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("!help - to receive a list of commands. \n");
        stringBuilder.append("!ping - to receive a pong from the bot. \n");

        member.getUser().openPrivateChannel().queue(privateChannel -> {

            messageService.sendMessageEmbed(member,
                    privateChannel,
                    "List of available Commands for this bot:",
                    stringBuilder.toString(),
                    Color.decode("#f7c315")
                );
        });
    }
}
