package org.example;

public class Geo {
    private String lat;
    private String lng;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Geo(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
