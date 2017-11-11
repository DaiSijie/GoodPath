package com.goodpaths.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Report {
    private double latitude;
    private double longitude;
    private Type problemType;

    public Report(@JsonProperty("problemtype") Type problemtype,
                  @JsonProperty("latitude") double latitude,
                  @JsonProperty("longitude") double longitude) {
        this.problemType = problemtype;
        this.longitude = longitude;
        if(this.longitude>180){
            this.longitude = this.longitude - 360;
        }
        this.latitude = latitude;
    }


    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public Type getProblemtype() {
        return this.problemType;
    }

    @Override
    public String toString() {
        return "Report{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", problemType='" + problemType + '\'' +
                '}';
    }

    public enum Type {
        HARASSMENT, ACCESSIBILITY;

        @Override
        public String toString() {
            return name();
        }
    }
}
