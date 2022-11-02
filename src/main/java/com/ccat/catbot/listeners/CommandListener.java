package com.ccat.catbot.listeners;

import com.ccat.catbot.commands.CommandManager;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommandListener extends ListenerAdapter {
    @Value("${discord.bot.prefix}")
    private String prefix;

    private final CommandManager commandManager;

    public CommandListener(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.isFromType(ChannelType.TEXT)) {

            String message = event.getMessage().getContentDisplay();
            TextChannel channel = event.getChannel().asTextChannel();

            if(message.startsWith(prefix)) {
                String[] args = message.substring(prefix.length()).split(" ");

                commandManager.executeCommand(
                        args[0],
                        event.getMember(),
                        event.getChannel().asTextChannel(),
                        event.getMessage());
            }
        }
    }
}
