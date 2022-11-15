package com.ccat.catbot.commands;

import com.ccat.catbot.listeners.PrivateMessageListener;
import com.ccat.catbot.model.entities.UserTime;
import com.ccat.catbot.model.services.TimezoneService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Optional;


public class ScheduleCommand implements ServerCommand {
    private final TimezoneService timezoneService;

    public ScheduleCommand(TimezoneService timezoneService) {
        this.timezoneService = timezoneService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        long userId = member.getUser().getIdLong();

        member.getUser().openPrivateChannel().queue(channel -> {

            Optional<UserTime> userTime = timezoneService.getUserTimezone(userId);
            final String state = (userTime.isPresent()? "Set Month" : "Set Timezone");

            final String queryMessage = (userTime
                    .map(time -> "Your currently registered Timezone is " + time.getTimeZone().getDisplayName() + ". To edit your Timezone, please use `!userdata time`. To select dates, please use `date`")
                    .orElse("You don't have a Timezone set, please enter your country and a city in your vicinity to start."));

            final UserTime userTimezone = new UserTime(userId, userTime.map(UserTime::getTimeZone).orElse(null));
            channel.sendMessage(queryMessage).queue();
            channel.getJDA()
                    .addEventListener(new PrivateMessageListener(timezoneService, channel.getIdLong(), userTimezone, state));

        });
    }
}
