package com.ccat.catbot.commands;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.model.dto.EventDataDto;
import com.ccat.catbot.model.dto.TimeEvaluation;
import com.ccat.catbot.model.entities.UserEventTime;
import com.ccat.catbot.model.services.MessageService;
import com.ccat.catbot.model.services.implementations.UserEventTimeService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RetrieveEventCommand implements ServerCommand{
    private final UserEventTimeService eventService;
    private final MessageService messageService;

    public RetrieveEventCommand(UserEventTimeService eventService, MessageService messageService) {
        this.eventService = eventService;
        this.messageService = messageService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        String[] args = message.getContentDisplay().split(" ");

        //TODO: Allow converting Times to desired ZoneId -> removes buildMemberTimesDisplay(List<LocalTime>)
//        ZoneId selectedZoneId = JdaConfiguration.INSTANCE.getServerZoneId();
//
//        if(args.length >= 3) {
//            try {
//                selectedZoneId = ZoneId.of(args[3]);
//            } catch (DateTimeException ex) {
//                textChannel.sendMessage("Requested Zone-Id is not valid. Times will be displayed in "
//                        + selectedZoneId + "  time.").queue();
//            }
//        }

        if(args.length >= 2) {
            try {
                long eventId = Long.parseLong(args[1]);
                Guild guild = textChannel.getGuild();

                Optional<ScheduledEvent> retrievedEvent = guild.getJDA().getScheduledEvents().stream()
                        .filter(event -> event.getIdLong() == eventId)
                        .findFirst();

                if(retrievedEvent.isEmpty()) {
                    messageService.sendMessageEmbed(member, textChannel,
                            "⚠ | Error. Event with the requested Event-Id: " + eventId + " doesn't exist.",
                            "Please verify if an Event with the requested Event-Id exists in the Events tab.",
                            Color.decode("#ff0000")
                    );
                    return;
                }
                ScheduledEvent scheduledEvent = retrievedEvent.get();

                Optional<EventDataDto> response = eventService.searchSuitableTime(new UserEventTime(eventId));

                if(response.isPresent()) {
                    EventDataDto eventData = response.get();

                    //Get all Participants merge allocated & unallocated:
                    Set<Long> userIds = Stream.concat(
                            eventData.getParticipants().stream(),
                            eventData.getUnallocatedParticipants().keySet().stream())
                            .collect(Collectors.toSet());

                    textChannel.sendTyping().queue();
                    guild.retrieveMembersByIds(userIds).onSuccess(members -> {
                        EmbedBuilder eventEmbed = new EmbedBuilder();

                        LocalDate suggestedEventDate = eventData.getEventDate();
                        Set<LocalTime> selectedHours = eventData.getAvailableHours();
                        HashMap<TimeEvaluation, LocalTime> timeEvaluation = eventData.getTimeEvaluation();

                        // POSSIBLE DATE:
                        eventEmbed.addField("Suggested Date:", suggestedEventDate.toString(), true);

                        // MIN/MAX/AVG:
                        StringBuilder evaluation = new StringBuilder();
                        timeEvaluation.forEach((k,v) -> {
                            evaluation.append(k).append(": ").append(v).append("\n");
                        });
                        eventEmbed.addField("Evaluated Times:", evaluation.toString(), true);

                        // Participant SELECTIONS:
                        StringBuilder selections = new StringBuilder();
                        selectedHours.forEach(t -> {
                            selections.append(t).append(" | ");
                        });
                        eventEmbed.addField("Selected Times:", selections.toString(), true);

                        // ALLOCATED Participants:
                        if(!eventData.getParticipants().isEmpty()) {
                            StringBuilder allocatedMemberDisplay = buildMemberTimesDisplay(members, eventData.getUserSelectedTimes());
                            eventEmbed.addField(
                                    "Possible Participants for the suggested event time:",
                                    allocatedMemberDisplay.toString(),
                                    false);
                        } else {
                            eventEmbed.setDescription("No possible matching Event-Times were found.");
                        }

                        // UNALLOCATED Participants:
                        if(!eventData.getUnallocatedParticipants().isEmpty()) {
                            HashMap<Long, List<LocalDateTime>> unallocatedParticipants = eventData.getUnallocatedParticipants();

                            eventEmbed.addBlankField(false);
                            StringBuilder unallocatedMemberDisplay = buildMemberDateTimesDisplay(members, unallocatedParticipants);

                            eventEmbed.addField(
                                    "Unallocated Participants:",
                                    unallocatedMemberDisplay.toString(),
                                    false);
                        }

                        // Displaying:
                        eventEmbed.setTitle(Emoji.fromUnicode("U+1F389").getAsReactionCode()
                                + scheduledEvent.getName()
                                + " created by: " + scheduledEvent.getCreator());
                        eventEmbed.setFooter("⚠ All times are currently displayed in "
                                + JdaConfiguration.INSTANCE.getServerZoneId() + " time.");
                        eventEmbed.setImage(scheduledEvent.getImageUrl());
                        eventEmbed.setColor(Color.decode("#35ecbf"));

                        textChannel.sendMessageEmbeds(eventEmbed.build()).queue();
                    });
                } else {
                    //No Data is present:
                    messageService.sendMessageEmbed(member,
                            textChannel,
                            "⚠ | Error. Data not found for requested EventId: " + eventId,
                            "No information was found. Participants have yet to register for this event.",
                            Color.decode("#ff0000")
                    );
                }
            } catch (NumberFormatException e){
                messageService.sendMessageEmbed(member,
                        textChannel,
                        "⚠ | Invalid Id.",
                        "The requested Id is not valid.",
                        Color.decode("#f7c315")
                );
            }
        } else {
            messageService.sendMessageEmbed(member,
                    textChannel,
                    "⚠ | Syntax Error.",
                    "The `!eventView` command, requires a valid Event-Id. `!eventView [event-Id]`.",
                    Color.decode("#f7c315")
            );
        }
    }

    private StringBuilder buildMemberDateTimesDisplay(List<Member> members, HashMap<Long, List<LocalDateTime>> participantTimes) {
        StringBuilder display = new StringBuilder();
        members.stream()
                .filter(m -> participantTimes.containsKey(m.getIdLong()))
                .forEach( m -> { //@Member : Time | Time | Time \n
                    display.append("Member: ").append(m.getAsMention()).append(" ")
                            .append(Emoji.fromUnicode("U+1F55C").getAsReactionCode())
                            .append("available: ");

                    List<LocalDateTime> memberTimes = participantTimes.get(m.getIdLong());
                    for(int i=0; i<memberTimes.size(); i++) {
                        display.append(memberTimes.get(i));

                        display.append((i == memberTimes.size()-1) ? "\n" : " | ");
                    }
                });
        return display;
    }

    private StringBuilder buildMemberTimesDisplay(List<Member> members, HashMap<Long, List<LocalTime>> participantTimes) {
        StringBuilder display = new StringBuilder();
        members.stream()
                .filter(m -> participantTimes.containsKey(m.getIdLong()))
                .forEach(m -> { //@Member : Time | Time | Time \n
                    display.append("Member: ").append(m.getAsMention()).append(" ")
                            .append(Emoji.fromUnicode("U+1F55C").getAsReactionCode())
                            .append("available: ");

                    List<LocalTime> memberTimes = participantTimes.get(m.getIdLong());
                    for(int i=0; i<memberTimes.size(); i++) {
                        display.append(memberTimes.get(i));

                        display.append((i == memberTimes.size()-1) ? "\n" : " | ");
                    }
                });
        return display;
    }
}
