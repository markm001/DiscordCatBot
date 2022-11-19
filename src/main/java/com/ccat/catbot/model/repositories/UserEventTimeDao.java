package com.ccat.catbot.model.repositories;

import com.ccat.catbot.model.entities.UserEventTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserEventTimeDao extends JpaRepository<UserEventTime, Long> {
    @Query("SELECT e FROM UserEventTime e WHERE eventId=:eventId")
    List<UserEventTime> findUserTimesForEventId(Long eventId);
}
