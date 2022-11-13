package com.ccat.catbot.clients.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class GeoFeature {
    @JsonProperty("type")
    private String type;
    @JsonProperty("properties")
    private GeoProperties properties;
    @JsonProperty("geometry")
    private Map<String,Object> geometry;
    @JsonProperty("bbox")
    private List<Double> bbox;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeoProperties getProperties() {
        return properties;
    }

    public void setProperties(GeoProperties properties) {
        this.properties = properties;
    }

    public Map<String, Object> getGeometry() {
        return geometry;
    }

    public void setGeometry(Map<String, Object> geometry) {
        this.geometry = geometry;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }
}
