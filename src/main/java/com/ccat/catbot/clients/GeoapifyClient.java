package com.ccat.catbot.clients;

import com.ccat.catbot.clients.model.GeoProperties;

import java.util.List;

public interface GeoapifyClient {
    List<GeoProperties> getDataByLocationString(String location);
}
