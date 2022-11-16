package com.ccat.catbot.model.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public interface EventService {
    CompletableFuture<ScheduledEvent> createNewTimedEvent(Guild guild, String topic, LocalDateTime startTime, LocalDateTime endTime);
}
