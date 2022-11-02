package com.ccat.catbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PingCommand implements ServerCommand{
    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        textChannel.sendMessage("pong" + member.getAsMention()).queue();
    }
}
