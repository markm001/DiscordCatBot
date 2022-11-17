package com.ccat.catbot.clients;

import com.ccat.catbot.clients.model.GeoFeature;
import com.ccat.catbot.clients.model.GeoProperties;
import com.ccat.catbot.clients.model.GeoapifyClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoapifyRestClient implements GeoapifyClient{
    @Value("${geoapify.baseUrl}")
    private String baseUrl;

    @Autowired
    private Environment env;

    public GeoapifyRestClient() {
    }

    @Override
    public List<GeoProperties> getDataByLocationString(String location) {
        RestTemplate restTemplate = new RestTemplate();

        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
        String requestUri = baseUrl + "/search?text=" + encodedLocation + "&apiKey=" + env.getProperty("apiKey");

        GeoapifyClientResponse response = restTemplate.getForObject(requestUri, GeoapifyClientResponse.class);

        //TODO: Handle Error Response Codes:
        assert response != null;

        return response.getFeatures().stream().map(GeoFeature::getProperties).collect(Collectors.toList());
    }
}
