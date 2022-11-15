package com.ccat.catbot.listeners;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.model.entities.UserTime;
import com.ccat.catbot.model.services.CalendarDisplayService;
import com.ccat.catbot.model.services.TimezoneService;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class PrivateMessageListener extends ListenerAdapter {
    private final TimezoneService timezoneService;
    private final Long channelId;
    private final Long userId;
    private String state;

    private UserTime userTime;

    private String userMonth;


    //TODO: Temporary - replace with Database-call later:
    private HashMap<Long, TimeZone> userTimeZones = new HashMap<>();

    public PrivateMessageListener(TimezoneService timezoneService, Long channelId, UserTime userTime, String state) {
        this.timezoneService = timezoneService;
        this.channelId = channelId;

        this.userTime = userTime;
        this.userId = userTime.getUserId();

        this.state = state;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            MessageChannelUnion channel = event.getChannel();
            if (event.getAuthor().getIdLong() == userId && channel.getIdLong() == channelId) {
                switch (state) {
                    case "Set Timezone": //Set User Timezone.
                        setUserTimezone(event, channel);
                        break;
                    case "Set Month": //Choose Month.
                        if(event.getMessage().getContentDisplay().equalsIgnoreCase("date")) {
                            setAvailableDate(event,channel);
                        }
                        break;
                    case "Set Day": //Choose Day & Time
                        System.out.println("Next");
                        break;
                }
            }
        }
    }

    private void setAvailableDate(MessageReceivedEvent event, MessageChannelUnion channel) {
        channel.sendMessage("Please select an available month")
                .addActionRow(getMonthSelectMenu())
                .queue(message -> {
                    JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(StringSelectInteractionEvent.class,
                        c -> c.getInteraction().getMessageIdLong() == message.getIdLong(),
                        a -> {
                            String selectedMonth = a.getInteraction().getValues().get(0);
                            String[] date = selectedMonth.split("-");
                            System.out.println("\n ## Selection was: " + date[0] + ":" + date[1] + " ## \n");

                            try {
                                int year = Integer.parseInt(date[0]);
                                int month = Integer.parseInt(date[1]);

                                a.reply(CalendarDisplayService.buildCalendarDisplay(year, month)).queue();

                                int lengthConstraint = LocalDate.of(year, month, 1).lengthOfMonth();
                                String regex;
                                switch(lengthConstraint) {
                                    case 28:
                                        regex = "^([1-9]|[12][0-8])$"; break;
                                    case 29:
                                        regex = "^([1-9]|[12]\\d)$"; break;
                                    case 30:
                                        regex = "^([1-9]|[12]\\d|30)$"; break;
                                    default:
                                        regex = "^([1-9]|[12]\\d|3[0-1])$"; break;
                                }

                                JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                                    c -> c.getMessage().getContentDisplay().matches(regex),
                                    messageReceivedEvent -> {
                                            int day = Integer.parseInt(messageReceivedEvent.getMessage().getContentRaw());
                                            LocalDate selectedDate = LocalDate.of(year, month, day);
                                            channel.sendMessage("Selected Date: " + selectedDate).queue();
                                    });
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        });
                });
    }

    private void setUserTimezone(MessageReceivedEvent event, MessageChannelUnion channel) {
        String location = event.getMessage().getContentDisplay();
        List<TimeZone> timeZoneResponse = timezoneService.getTimeZoneFromLocation(location);

        if (timeZoneResponse.isEmpty()) {
            channel.sendMessage("Location was not found, please try to be more specific.").queue();
        } else if(timeZoneResponse.size() > 1) {
            channel.sendMessage("Multiple results were found. Please choose your Timezone.")
                    .addActionRow(getTimezoneSelectMenu(timeZoneResponse))
                    .queue(message -> JdaConfiguration.INSTANCE.getEventWaiter()
                            .waitForEvent(StringSelectInteractionEvent.class,
                                c -> c.getInteraction().getMessageIdLong() == message.getIdLong(),
                                a -> {
                                    String selectedTimezone = a.getInteraction().getValues().get(0);
                                    TimeZone timeZone = TimeZone.getTimeZone(selectedTimezone);

                                    userTime = new UserTime(
                                            userId,
                                            timeZone);
                                    timezoneService.saveUserTimezone(userTime);
                                    a.reply("Your Timezone has been set to: " + timeZone.getDisplayName() + ".\n To select dates, please type `date`.")
                                            .queue();
                                    state = "Set Month";
                                }));
        } else {
            userTime = new UserTime(
                    userId,
                    timeZoneResponse.get(0));
            timezoneService.saveUserTimezone(userTime);
            channel.sendMessage("Your Timezone has been set to: " + userTime.getTimeZone().getDisplayName() + ".\n To select dates, please type `date`.")
                    .queue();
            state = "Set Month";
        }
    }

    public static StringSelectMenu getMonthSelectMenu() {
        //current Date:
        LocalDate now = LocalDate.now();
        int monthsInAdvance = 4;

        List<SelectOption> options = new ArrayList<>();

        for (int i = 0; i < monthsInAdvance; i++) {
            options.add(SelectOption.of(now.plusMonths(i).getMonth().toString(), now.plusMonths(i).toString()));
        }

        return StringSelectMenu.create("Month options")
                .addOptions(options)
                .build();

    }

    private static StringSelectMenu getTimezoneSelectMenu(List<TimeZone> timeZones) {
        HashSet<TimeZone> uniqueTimeZones = new HashSet<>(timeZones);

        List<SelectOption> options = uniqueTimeZones.stream()
                .map(time -> SelectOption.of(time.getDisplayName(), time.getID()))
                .collect(Collectors.toList());

        return StringSelectMenu.create("Pick a timezone")
                .addOptions(options)
                .build();
    }
}
