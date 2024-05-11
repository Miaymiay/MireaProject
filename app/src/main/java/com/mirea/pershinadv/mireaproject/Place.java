package com.mirea.pershinadv.mireaproject;

import org.osmdroid.util.GeoPoint;

public class Place {
    String name;
    String description;
    String address;
    GeoPoint location;

    public Place(String name, String description, String address, GeoPoint location) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public GeoPoint getLocation() {
        return location;
    }
}
