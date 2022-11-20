package com.ccat.catbot.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class EventDataDto {
    Long eventId;
    LocalDate eventDate;
    Set<LocalTime> availableHours;
    Set<Long> participants;
    HashMap<Long, List<LocalTime>> userSelectedTimes;
    HashMap<TimeEvaluation,LocalTime> timeEvaluation;

    HashMap<Long, List<LocalDateTime>> unallocatedParticipants;

    public EventDataDto() {
    }

    public EventDataDto(Long eventId, LocalDate eventDate, Set<LocalTime> availableHours, Set<Long> participants, HashMap<Long, List<LocalTime>> userSelectedTimes, HashMap<TimeEvaluation, LocalTime> timeEvaluation, HashMap<Long, List<LocalDateTime>> unallocatedParticipants) {
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.availableHours = availableHours;
        this.participants = participants;
        this.userSelectedTimes = userSelectedTimes;
        this.timeEvaluation = timeEvaluation;
        this.unallocatedParticipants = unallocatedParticipants;
    }

    public Long getEventId() {
        return eventId;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public Set<LocalTime> getAvailableHours() {
        return availableHours;
    }

    public Set<Long> getParticipants() {
        return participants;
    }

    public HashMap<Long, List<LocalTime>> getUserSelectedTimes() {
        return userSelectedTimes;
    }

    public HashMap<TimeEvaluation, LocalTime> getTimeEvaluation() {
        return timeEvaluation;
    }

    public HashMap<Long, List<LocalDateTime>> getUnallocatedParticipants() {
        return unallocatedParticipants;
    }
}
