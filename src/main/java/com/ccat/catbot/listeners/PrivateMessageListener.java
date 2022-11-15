package com.ccat.catbot.listeners;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.listeners.enums.SchedulerState;
import com.ccat.catbot.model.entities.UserEventTime;
import com.ccat.catbot.model.entities.UserTime;
import com.ccat.catbot.model.services.CalendarDisplayService;
import com.ccat.catbot.model.services.TimezoneService;
import com.ccat.catbot.model.services.UserEventTimeService;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;


public class PrivateMessageListener extends ListenerAdapter {
    private final TimezoneService timezoneService;
    private final UserEventTimeService eventTimeService;
    private final Long channelId;
    private final Long userId;
    private final Long eventId;
    private SchedulerState state;

    private UserTime userTime;

    private List<LocalDate> userDates;

    public PrivateMessageListener(TimezoneService timezoneService, UserEventTimeService eventTimeService, Long channelId, Long eventId, UserTime userTime, SchedulerState state) {
        this.timezoneService = timezoneService;
        this.eventTimeService = eventTimeService;
        this.channelId = channelId;
        this.eventId = eventId;
        this.userTime = userTime;
        this.userId = userTime.getUserId();

        this.state = state;

        this.userDates = new ArrayList<>();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            MessageChannelUnion channel = event.getChannel();
            if (event.getAuthor().getIdLong() == userId && channel.getIdLong() == channelId) {
                switch (state) {
                    case TIMEZONE: //Set User Timezone.
                        setUserTimezone(event, channel);
                        break;
                    case MONTH: //Choose Month and Day:
                        if (event.getMessage().getContentDisplay().equalsIgnoreCase("add date")) {
                            setAvailableDate(channel);
                            if (userDates.size() == 1) {
                                JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                                        c -> c.getMessage().getContentDisplay().equalsIgnoreCase("complete"),
                                        a -> state = SchedulerState.DAY);
                            }
                        }
                        break;
                    case DAY: //Choose Time for Day:
                        for (LocalDate date : userDates) {
                            System.out.println(date);
                            channel.sendMessage("Select a Time for the " + date)
                                    .addActionRow(getHourSelectMenu())
                                    .queue(message ->
                                            JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(StringSelectInteractionEvent.class,
                                                    con -> con.getInteraction().getMessageIdLong() == message.getIdLong(),
                                                    act -> {
                                                        String selectedTime = act.getInteraction().getValues().get(0);
                                                        try {
                                                            LocalTime time = LocalTime.parse(selectedTime);
                                                            LocalDateTime dateTime = LocalDateTime.of(date, time);

                                                            ZonedDateTime zonedUserTime = ZonedDateTime.of(dateTime, userTime.getTimeZone().toZoneId());
                                                            ZonedDateTime zonedServerTime = zonedUserTime.withZoneSameInstant(JdaConfiguration.INSTANCE.getServerZoneId());

                                                            eventTimeService.saveEventTime(new UserEventTime(userId, eventId, zonedServerTime));

                                                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                                            act.reply("Your selection has been saved: " + dateTime.format(dateTimeFormatter)).queue();

                                                        } catch (DateTimeParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }));
                        }
                        break;
                }
            }
        }
    }

    private void setAvailableDate(MessageChannelUnion channel) {
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
                                    switch (lengthConstraint) {
                                        case 28:
                                            regex = "^([1-9]|[12][0-8])$";
                                            break;
                                        case 29:
                                            regex = "^([1-9]|[12]\\d)$";
                                            break;
                                        case 30:
                                            regex = "^([1-9]|[12]\\d|30)$";
                                            break;
                                        default:
                                            regex = "^([1-9]|[12]\\d|3[0-1])$";
                                            break;
                                    }

                                    JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(MessageReceivedEvent.class,
                                            c -> c.getMessage().getContentDisplay().matches(regex),
                                            messageReceivedEvent -> {
                                                int day = Integer.parseInt(messageReceivedEvent.getMessage().getContentRaw());
                                                LocalDate selectedDate = LocalDate.of(year, month, day);
                                                channel.sendMessage("Selected Date: " + selectedDate
                                                        + "\n To confirm your selection, please type `complete`").queue();
                                                userDates.add(selectedDate);
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
        } else if (timeZoneResponse.size() > 1) {
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
                                        a.reply("Your Timezone has been set to: " + timeZone.getDisplayName() + ".\n To select dates, please type `add date`.")
                                                .queue();
                                        state = SchedulerState.MONTH;
                                    }));
        } else {
            userTime = new UserTime(
                    userId,
                    timeZoneResponse.get(0));
            timezoneService.saveUserTimezone(userTime);
            channel.sendMessage("Your Timezone has been set to: " + userTime.getTimeZone().getDisplayName() + ".\n To select dates, please type `add date`.")
                    .queue();
            state = SchedulerState.MONTH;
        }
    }

    public static StringSelectMenu getHourSelectMenu() {
        List<SelectOption> options = new ArrayList<>();


        LocalTime time = LocalTime.of(0, 0);
        for (int i = 0; i < 24; i++) {
            String timeOption = time.plusHours(i).toString();
            options.add(SelectOption.of(timeOption, timeOption));
        }
        return StringSelectMenu.create("Select a time.")
                .addOptions(options)
                .build();
    }

    public static StringSelectMenu getMonthSelectMenu() {
        //current Date:
        LocalDate now = LocalDate.now();
        int monthsInAdvance = 4;

        List<SelectOption> options = new ArrayList<>();

        for (int i = 0; i < monthsInAdvance; i++) {
            options.add(SelectOption.of(now.plusMonths(i).getMonth().toString(), now.plusMonths(i).toString()));
        }

        return StringSelectMenu.create("Select a month.")
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
