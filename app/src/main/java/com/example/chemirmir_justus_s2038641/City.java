package com.example.chemirmir_justus_s2038641;

//
// Name                 ________ Justus Wamswa Chemirmir
// Student ID           ________ S2038641
// Programme of Study   ________ BSc (Hons) Computing
//
// class representing a city
public class City {
    private int position;
    private int id;
    private String name;
    private String country;

    public City(int position, int id, String name, String country) {
        this.position = position;
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public int getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
}
