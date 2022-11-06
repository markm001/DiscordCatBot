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
        stringBuilder.append("`!ping` - receive a pong from the bot. \n");
        stringBuilder.append("`!purge [amount]` - delete a specific amount of messages. \n");
        stringBuilder.append("`!createrole [name] [#color]` - create a role with a hexadecimal color. \n");
        stringBuilder.append("`!reactrole [channel-Id] [message-Id] [role-Id] [emote]` - to allow users to receive roles via reactions on a specific message. \n");

        member.getUser().openPrivateChannel().queue(privateChannel -> {

            messageService.sendMessageEmbed(member,
                    privateChannel,
                    "List of all available Commands for this bot:",
                    stringBuilder.toString(),
                    Color.decode("#f7c315")
                );
        });
    }
}
