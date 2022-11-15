package com.ccat.catbot.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.TimeZone;

@Entity
@Table(name="usertimezones")
public class UserTime {
    @Id
    Long userId;
    TimeZone timeZone;

    public UserTime() {
    }

    public UserTime(Long userId, TimeZone timeZone) {
        this.userId = userId;
        this.timeZone = timeZone;
    }

    public Long getUserId() {
        return userId;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }
}
