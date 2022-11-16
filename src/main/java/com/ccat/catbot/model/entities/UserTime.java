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
    TimeZone timezone;

    public UserTime() {
    }

    public UserTime(Long userId, TimeZone timezone) {
        this.userId = userId;
        this.timezone = timezone;
    }

    public Long getUserId() {
        return userId;
    }

    public TimeZone getTimezone() {
        return timezone;
    }
}
