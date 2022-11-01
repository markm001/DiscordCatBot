package com.ccat.catbot.listeners;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CommandListener extends ListenerAdapter {
    @Value("${discord.bot.prefix}")
    private String prefix;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isFromType(ChannelType.TEXT)) {

            String message = event.getMessage().getContentDisplay();
            TextChannel channel = event.getChannel().asTextChannel();

            if(message.startsWith(prefix)) {
                String[] args = message.substring(prefix.length()).split(" ");

                if(Arrays.stream(args).anyMatch(a -> a.contains("ping"))) {
                    channel.sendMessage("pong").queue();
                }

            }
        }
    }
}
