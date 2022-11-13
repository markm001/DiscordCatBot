package com.ccat.catbot.clients.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GeoapifyClientResponse {
    @JsonProperty("type")
    private String type;
    @JsonProperty("features")
    private List<GeoFeature> features;
    @JsonProperty("query")
    private Map<String,Object> query;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GeoFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoFeature> features) {
        this.features = features;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }
}
