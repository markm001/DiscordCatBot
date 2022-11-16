package com.ccat.catbot.model.services.implementations;

import com.ccat.catbot.clients.GeoapifyClient;
import com.ccat.catbot.clients.GeoapifyMockClient;
import com.ccat.catbot.clients.model.GeoProperties;
import com.ccat.catbot.model.entities.UserTime;
import com.ccat.catbot.model.repositories.UserTimeDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class TimezoneService {
    private final GeoapifyClient client;
    private final UserTimeDao timeDao;

    public TimezoneService(GeoapifyMockClient client, UserTimeDao timeDao) {
        this.client = client;
        this.timeDao = timeDao;
    }

    public List<TimeZone> getTimeZoneFromLocation(String location) {
        List<GeoProperties> propertiesList = client.getDataByLocationString(location);

        return propertiesList.stream()
                .map(properties -> TimeZone.getTimeZone(properties.getTimezone().getName()))
                .collect(Collectors.toList());
    }

    public UserTime saveUserTimezone(UserTime request) {
        return timeDao.save(new UserTime(
                        request.getUserId(),
                        request.getTimezone()));
    }

    public Optional<UserTime> getUserTimezone(Long userId) {
        return timeDao.findById(userId);
    }
}
