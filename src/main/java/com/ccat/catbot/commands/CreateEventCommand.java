package com.ccat.catbot.commands;

import com.ccat.catbot.model.services.EventService;
import com.ccat.catbot.model.services.MessageService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateEventCommand implements ServerCommand{
    private final EventService eventService;
    private final MessageService messageService;

    public CreateEventCommand(EventService eventService, MessageService messageService) {
        this.eventService = eventService;
        this.messageService = messageService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        //Syntax: !eventCreate [topic] ([startTime: 2022-11-29T20:00:00] [duration] in UTC)
        if(member.hasPermission(Permission.MANAGE_EVENTS)) {
            //TODO:Retrieve User-Timezone from DB -> Set ZoneId for User
            Guild guild = textChannel.getGuild();

            Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
            Matcher matcher = pattern.matcher(message.getContentDisplay());


            ArrayList<String> argsList = new ArrayList<>();
            while(matcher.find()) { //[!eventCreate, "Relaxing, a test", 2022-11-16T20:00:00, 4]
                argsList.add(matcher.group());
            }
            String topic = "";
            if(argsList.size() >= 2) {
                topic = argsList.get(1);
            }  else {
                sendError(member, textChannel);
                return;
            }

            if(argsList.size() == 4) {
                String[] startStrings = argsList.get(2).split("T");
                try {
                    LocalDate startDate = LocalDate.parse(startStrings[0]);
                    LocalTime startTime = LocalTime.parse(startStrings[1]);
                    long durationHours = Long.parseLong(argsList.get(3));

                    LocalDateTime startLdt = LocalDateTime.of(startDate, startTime);
                    LocalDateTime endLdt = startLdt.plusHours(durationHours);

                    createEvent(guild, topic, startLdt, endLdt);
                } catch (DateTimeParseException | NumberFormatException e) {
                    sendError(member, textChannel);
                }
            } else if (argsList.size() == 2) {
                LocalDateTime startLdt = LocalDateTime.now().plusMonths(1);
                LocalDateTime endLdt = startLdt.plusHours(8);

                createEvent(guild, topic, startLdt, endLdt);
            }
        } else {
            messageService.sendAccessDenied(member, textChannel, "you cannot schedule events.");
        }
    }

    private void sendError(Member member, TextChannel textChannel) {
        messageService.sendMessageEmbed(member,
                textChannel,
                "⚠ | Syntax Error.",
                "The `!eventCreate` command, requires a topic optional:(startDateTime-UTC[2022-11-29T20:00:00] and duration[hours]). `!eventCreate [\"topic\"] [startTime in UTC] [duration in Hours]`.",
                Color.decode("#f7c315")
        );
    }

    private void createEvent(Guild guild, String topic, LocalDateTime startLdt, LocalDateTime endLdt) {
        eventService.createNewTimedEvent(guild, topic, startLdt, endLdt)
                .thenAccept(event -> {
                    long eventId = event.getIdLong();
                    event.getManager().setDescription("Please use the ID: [" + eventId
                            + "], when scheduling or retrieving Information. \n To start type: `!schedule " + eventId + "`."
                            + "\n To view event statistics type: `!eventView "+ eventId +"`").queue();
                });
    }
}
