package com.ccat.catbot.clients.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoTimezoneData {
    @JsonProperty("name")
    private String name;
    @JsonProperty("offset_STD")
    private String offsetSTD;
    @JsonProperty("offset_STD_seconds")
    private Integer offsetSTDSeconds;
    @JsonProperty("offset_DST")
    private String offsetDST;
    @JsonProperty("offset_DST_seconds")
    private Integer offsetDSTSeconds;
    @JsonProperty("abbreviation_STD")
    private String abbreviationSTD;
    @JsonProperty("abbreviation_DST")
    private String abbreviationDST;

    public GeoTimezoneData() {
    }

    public GeoTimezoneData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffsetSTD() {
        return offsetSTD;
    }

    public void setOffsetSTD(String offsetSTD) {
        this.offsetSTD = offsetSTD;
    }

    public Integer getOffsetSTDSeconds() {
        return offsetSTDSeconds;
    }

    public void setOffsetSTDSeconds(Integer offsetSTDSeconds) {
        this.offsetSTDSeconds = offsetSTDSeconds;
    }

    public String getOffsetDST() {
        return offsetDST;
    }

    public void setOffsetDST(String offsetDST) {
        this.offsetDST = offsetDST;
    }

    public Integer getOffsetDSTSeconds() {
        return offsetDSTSeconds;
    }

    public void setOffsetDSTSeconds(Integer offsetDSTSeconds) {
        this.offsetDSTSeconds = offsetDSTSeconds;
    }

    public String getAbbreviationSTD() {
        return abbreviationSTD;
    }

    public void setAbbreviationSTD(String abbreviationSTD) {
        this.abbreviationSTD = abbreviationSTD;
    }

    public String getAbbreviationDST() {
        return abbreviationDST;
    }

    public void setAbbreviationDST(String abbreviationDST) {
        this.abbreviationDST = abbreviationDST;
    }
}
