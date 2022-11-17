package com.ccat.catbot.commands;

import com.ccat.catbot.listeners.PrivateMessageListener;
import com.ccat.catbot.listeners.enums.SchedulerState;
import com.ccat.catbot.model.entities.UserTime;
import com.ccat.catbot.model.services.MessageService;
import com.ccat.catbot.model.services.implementations.TimezoneService;
import com.ccat.catbot.model.services.implementations.UserEventTimeService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


public class ScheduleCommand implements ServerCommand {
    private final TimezoneService timezoneService;
    private final UserEventTimeService eventTimeService;

    private final MessageService messageService;

    public ScheduleCommand(TimezoneService timezoneService, UserEventTimeService eventTimeService, MessageService messageService) {
        this.timezoneService = timezoneService;
        this.eventTimeService = eventTimeService;
        this.messageService = messageService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        String[] args = message.getContentDisplay().split(" ");
        if (args.length == 2) {
            try {
                long eventId = Long.parseLong(args[1]);
                long userId = member.getUser().getIdLong();

                //Requested Event-Id exists in for Guild check:
                if(textChannel.getGuild().getScheduledEvents().stream()
                        .noneMatch(scheduledEvent -> scheduledEvent.getIdLong() == eventId)) {
                    messageService.sendMessageEmbed(member,
                            textChannel,
                            "⚠ | Event-Id Error.",
                            "The entered Event-Id is not valid. Please check your active Guild Events.",
                            Color.decode("#f7c315"));
                    return;
                }

                message.delete().queueAfter(10, TimeUnit.SECONDS);
                member.getUser().openPrivateChannel().queue(channel -> {

                    Optional<UserTime> userTime = timezoneService.getUserTimezone(userId);
                    final SchedulerState state = (userTime.isPresent() ? SchedulerState.MONTH : SchedulerState.TIMEZONE);

                    final String queryMessage = (userTime
                            .map(time -> "Your currently registered Timezone is " + time.getTimezone().getDisplayName() + ". To edit your Timezone, please use `!userdata time`. To select dates, please use `date`")
                            .orElse("You don't have a Timezone set, please enter your country and a city in your vicinity to start."));

                    final UserTime userTimezone = new UserTime(userId, userTime.map(UserTime::getTimezone).orElse(null));
                    channel.sendMessage(queryMessage).queue();
                    channel.getJDA()
                            .addEventListener(new PrivateMessageListener(timezoneService, eventTimeService, channel.getIdLong(), eventId, userTimezone, state));

                });
            } catch (NumberFormatException e) {
                messageService.sendMessageEmbed(
                        member,
                        textChannel,
                        "⚠ | NumberFormatException.",
                        "The requested Id is not a valid value.",
                        Color.decode("#f7c315"));
            }
        } else {
            messageService.sendMessageEmbed(member,
                    textChannel,
                    "⚠ | Syntax Error.",
                    "The `!schedule` command, requires a valid event-ID. `!schedule [event-ID]`.",
                    Color.decode("#f7c315"));
        }
    }
}
