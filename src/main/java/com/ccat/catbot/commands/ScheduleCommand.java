package com.ccat.catbot.commands;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.model.services.TimezoneService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class ScheduleCommand implements ServerCommand{
    private final TimezoneService timezoneService;

    public ScheduleCommand(TimezoneService timezoneService) {
        this.timezoneService = timezoneService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        long memberId = member.getIdLong();

        member.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage("To schedule a time a timezone is required. \n" +
                    "Please enter a city in your vicinity to set a timezone.").queue();

            JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                    c -> (c.getChannel() == channel) && (c.getAuthor().getIdLong() == memberId),
                    a -> {
                        String locationString = a.getMessage().getContentDisplay();
                        List<TimeZone> timeZones = timezoneService.getTimeZoneFromLocation(locationString);
//                        List<TimeZone> timeZones = List.of(
//                                TimeZone.getTimeZone("America/Chicago"),
//                                TimeZone.getTimeZone("America/Glace_Bay"));

                        System.out.println("\n ### Location String: '" + locationString + "' ### \n");

                        if(timeZones.isEmpty()) {
                            channel.sendMessage("Entry ambiguous | Please be more specific").queue();
                        }

                        if(timeZones.size() > 1) {
                            channel.sendMessage("Multiple entries were found. Please pick your timezone of choice.")
                                    .addActionRow(getSelectMenu(timeZones)).queue();
                        } else {
                            channel.sendMessage("Timezone acquired." + timeZones.get(0)).queue();
                        }
                    });
        });
    }

    private static StringSelectMenu getSelectMenu(List<TimeZone> timeZones) {
        HashSet<TimeZone> uniqueTimeZones = new HashSet<>(timeZones);

        List<SelectOption> options = uniqueTimeZones.stream()
                .map(time -> SelectOption.of(time.getDisplayName(), time.getDisplayName()))
                .collect(Collectors.toList());

        return StringSelectMenu.create("Pick a timezone")
                .addOptions(options)
                .build();
    }
}
