package com.ccat.catbot.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name="usereventtimes")
public class UserEventTime {
    @Id
    private Long id;
    private Long userId;
    private Long eventId;

    //TODO: replace with List<ZoneDateTime> ?
    private ZonedDateTime availableTime;


    public UserEventTime() {
    }

    public UserEventTime(Long id, Long userId, Long eventId, ZonedDateTime availableTime) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.availableTime = availableTime;
    }

    public UserEventTime(Long userId, Long eventId, ZonedDateTime availableTime) {
        this.id = UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE;
        this.userId = userId;
        this.eventId = eventId;
        this.availableTime = availableTime;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public ZonedDateTime getAvailableTime() {
        return availableTime;
    }

    public Long getEventId() {
        return eventId;
    }
}
