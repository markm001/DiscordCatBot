package com.ccat.catbot.model.services.implementations;

import com.ccat.catbot.model.entities.UserEventTime;
import com.ccat.catbot.model.repositories.UserEventTimeDao;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserEventTimeService {
    private final UserEventTimeDao eventTimeDao;

    public UserEventTimeService(UserEventTimeDao eventTimeDao) {
        this.eventTimeDao = eventTimeDao;
    }

    public UserEventTime saveEventTime(UserEventTime request) {
        return eventTimeDao.save(new UserEventTime(
                UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE,
                request.getUserId(),
                request.getEventId(),
                request.getAvailableTime()
        ));
    }
}
