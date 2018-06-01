package com.swg.jalinatm.POJO;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Andrew Widjaja on 5/28/2018.
 */

public class Vendor {

    private String id;
    private String name;
    private String org;
    private String description;
    private LatLng loc;
    private LatLng prevLoc;
    private Long lastUpdateTime;

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

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

    public LatLng getPrevLoc() {
        return prevLoc;
    }

    public void setPrevLoc(LatLng prevLoc) {
        this.prevLoc = prevLoc;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
