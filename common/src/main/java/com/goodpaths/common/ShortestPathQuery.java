package com.goodpaths.common;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ShortestPathQuery {
    private final MyLngLat start;
    private final MyLngLat end;
    private final Report.Type problemtype;

    public ShortestPathQuery(@JsonProperty("start") MyLngLat start, @JsonProperty("end") MyLngLat end, @JsonProperty("problemtype") Report.Type problemtype) {
        this.start = start;
        this.end = end;
        this.problemtype = problemtype;
    }

    public MyLngLat getStart() {
        return start;
    }

    public MyLngLat getEnd() {
        return end;
    }

    public Report.Type getProblemtype() {
        return problemtype;
    }
}
