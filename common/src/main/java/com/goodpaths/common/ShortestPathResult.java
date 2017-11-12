package com.goodpaths.common;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ShortestPathResult {
    private List<MyLngLat> nodes;

    public ShortestPathResult(@JsonProperty("nodes") List<MyLngLat> nodes) {
        this.nodes = nodes;
    }

    public List<MyLngLat> getNodes() {
        return nodes;
    }
}
