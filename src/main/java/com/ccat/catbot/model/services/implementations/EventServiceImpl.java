package com.ccat.catbot.model.services.implementations;

import com.ccat.catbot.model.entities.UserEventTime;
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
}
