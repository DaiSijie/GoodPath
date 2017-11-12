package com.goodpaths.common;


import com.fasterxml.jackson.annotation.JsonProperty;

public class MyLngLat {
    public double lng;
    public double lat;

    public MyLngLat(@JsonProperty("lng") double lng, @JsonProperty("lat") double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }
}
