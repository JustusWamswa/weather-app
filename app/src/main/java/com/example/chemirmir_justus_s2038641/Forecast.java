package com.example.chemirmir_justus_s2038641;

//
// Name                 ________ Justus Wamswa Chemirmir
// Student ID           ________ S2038641
// Programme of Study   ________ BSc (Hons) Computing
//

// class representing weather forecast
public class Forecast {
    private int day;
    private String title;
    private String description;
    private String pubDate;
    private String geoLocation;

    public Forecast(int day, String title, String description, String pubDate, String geoLocation) {
        this.day = day;
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.geoLocation = geoLocation;
    }

    public int getDay() {
        return day;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

}
