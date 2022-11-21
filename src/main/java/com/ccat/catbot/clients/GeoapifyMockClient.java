package com.ccat.catbot.clients;

import com.ccat.catbot.clients.model.GeoProperties;
import com.ccat.catbot.clients.model.GeoTimezoneData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("test")
public class GeoapifyMockClient implements GeoapifyClient{
    @Override
    public List<GeoProperties> getDataByLocationString(String location) {
        return List.of(
                new GeoProperties(new GeoTimezoneData("America/Glace_Bay")),
                new GeoProperties(new GeoTimezoneData("Australia/Sydney")),
                new GeoProperties(new GeoTimezoneData("America/Chicago")));
    }
}
