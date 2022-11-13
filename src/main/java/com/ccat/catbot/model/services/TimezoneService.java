package com.ccat.catbot.model.services;

import com.ccat.catbot.clients.GeoapifyClient;
import com.ccat.catbot.clients.GeoapifyMockClient;
import com.ccat.catbot.clients.model.GeoProperties;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class TimezoneService {
    private final GeoapifyClient client;

    public TimezoneService(GeoapifyMockClient client) {
        this.client = client;
    }

    public List<TimeZone> getTimeZoneFromLocation(String location) {
        List<GeoProperties> propertiesList = client.getDataByLocationString(location);

        int resultSize = propertiesList.size();

        if(resultSize > 20) {
            return Collections.emptyList();
        }

        return propertiesList.stream()
                .map(properties -> TimeZone.getTimeZone(properties.getTimezone().getName()))
                .collect(Collectors.toList());
    }
}
