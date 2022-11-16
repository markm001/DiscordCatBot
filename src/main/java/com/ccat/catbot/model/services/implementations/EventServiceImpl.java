package com.ccat.catbot.model.services.implementations;

import com.ccat.catbot.model.services.EventService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public CompletableFuture<ScheduledEvent> createNewTimedEvent(Guild guild, String topic, LocalDateTime startTime, LocalDateTime endTime) {
        OffsetDateTime startOffsetTime = OffsetDateTime.of(startTime, ZoneOffset.UTC);
        OffsetDateTime endOffsetTime = OffsetDateTime.of(endTime, ZoneOffset.UTC);

        return guild.createScheduledEvent(
                topic,
                Objects.requireNonNull(guild.getDefaultChannel()).getName(),
                startOffsetTime,
                endOffsetTime
        ).submit();
    }



    public void createNewEvent(Guild guild, LocalDateTime startTime, LocalDateTime endTime, String topic, String description) {
//        LocalDateTime futureTime = LocalDateTime.now().plusMonths(1);
//        guild.createScheduledEvent(
//                topic,
//                Objects.requireNonNull(guild.getDefaultChannel()).getName(),
//                OffsetDateTime.of(futureTime, ZoneOffset.UTC),
//                OffsetDateTime.of(futureTime, ZoneOffset.UTC).plusMonths(1)
//        ).queue(event -> {
//            long eventId = event.getIdLong();
//            event.getManager().setDescription("Please use the ID: [" + eventId
//                    + "], when scheduling or retrieving Information. \n To start type: `!schedule " + eventId + "`").queue();
//        });
    }
}
