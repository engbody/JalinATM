package com.swg.jalinatm.POJO;

import android.location.Location;

/**
 * Created by Andrew Widjaja on 5/28/2018.
 */

public class Vendor {

    private String id;
    private String name;
    private String org;
    private String description;
    private Location loc;

    public Vendor(String id, String name, String org) {
        this.id = id;
        this.name = name;
        this.org = org;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }
}