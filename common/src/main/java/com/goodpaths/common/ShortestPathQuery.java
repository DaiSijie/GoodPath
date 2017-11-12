package com.goodpaths.common;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortestPathQuery {
    private final MyLngLat start;
    private final MyLngLat end;

    public ShortestPathQuery(@JsonProperty("start") MyLngLat start, @JsonProperty("end") MyLngLat end) {
        this.start = start;
        this.end = end;
    }

    public MyLngLat getStart() {
        return start;
    }

    public MyLngLat getEnd() {
        return end;
    }
}
