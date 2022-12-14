package com.ccat.catbot.commands;

import com.ccat.catbot.model.services.*;
import com.ccat.catbot.model.services.implementations.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandManager {

    private final ConcurrentHashMap<String, ServerCommand> commandMap;
    private final MessageService messageService;

    public CommandManager(MessageService messageService,
                          ReactRoleService reactRoleService,
                          EmoteRetrieveService emoteService,
                          MemberPermissionService permissionService,
                          ServerChannelService serverChannelService,
                          DiceRollService diceRollService,
                          TimezoneService timezoneService,
                          UserEventTimeService eventTimeService,
                          EventService eventCreateService) {
        this.messageService = messageService;

        commandMap = new ConcurrentHashMap<>();
        commandMap.put("ping", new PingCommand());
        commandMap.put("help", new HelpCommand());
        commandMap.put("purge", new PurgeCommand(messageService));
        commandMap.put("createrole", new RoleCreateCommand(messageService, permissionService));
        commandMap.put("reactrole", new ReactRoleCommand(messageService, reactRoleService, emoteService, permissionService));
        commandMap.put("permit", new MemberPermissionCommand(messageService, permissionService));
        commandMap.put("roll", new DiceRollCommand(diceRollService, messageService));
        commandMap.put("specify", new SpecifyCommand(messageService, serverChannelService));
        commandMap.put("schedule", new ScheduleCommand(timezoneService, eventTimeService,messageService));
        commandMap.put("eventcreate", new CreateEventCommand(eventCreateService,messageService));
        commandMap.put("eventview", new RetrieveEventCommand(eventTimeService,messageService));
    }

    public void executeCommand(String command, Member member, TextChannel channel, Message message) {
        if(commandMap.containsKey(command.toLowerCase())) {
            ServerCommand serverCommand = commandMap.get(command.toLowerCase());
            serverCommand.executeCommand(member, channel, message);
        }
        else {
            messageService.sendMessageEmbed(member,
                    channel,
                    "??? | Error, command not found.",
                    "You entered an invalid command. Use '!help' for a full list of valid commands.",
                    Color.decode("#f7c315"));
        }
    }
}
