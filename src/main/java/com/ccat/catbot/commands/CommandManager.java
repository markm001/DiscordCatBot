package com.ccat.catbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandManager {

    private final ConcurrentHashMap<String, ServerCommand> commandMap;

    public CommandManager() {
        commandMap = new ConcurrentHashMap<>();
        commandMap.put("ping", new PingCommand());
        commandMap.put("help", new HelpCommand());
        commandMap.put("purge", new PurgeCommand());
    }

    public void executeCommand(String command, Member member, TextChannel channel, Message message) {
        if(commandMap.containsKey(command.toLowerCase())) {
            ServerCommand serverCommand = commandMap.get(command.toLowerCase());
            serverCommand.executeCommand(member, channel, message);
        }
        else {
            channel.sendMessage("Invalid Command has been entered. Use: '!help' for a full list of valid commands.").queue();
        }
    }
}
