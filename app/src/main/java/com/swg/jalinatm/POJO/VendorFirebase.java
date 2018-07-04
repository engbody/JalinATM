package com.swg.jalinatm.POJO;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by Andrew Widjaja on 7/2/2018.
 */

@IgnoreExtraProperties
@Parcel
public class VendorFirebase {

    private ArrayList<Long> ATMs;
    private ArrayList<Long> center;
    private String name;
    private int status;
    private Long timestamp;
    private int zoom_level;
    private String key;

    public VendorFirebase(ArrayList<Long> ATMs, ArrayList<Long> center, String name, int status, Long timestamp, int zoom_level) {
        this.ATMs = ATMs;
        this.center = center;
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
        this.zoom_level = zoom_level;
    }

    public VendorFirebase() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<Long> getATMs() {
        return ATMs;
    }

    public void setATMs(ArrayList<Long> ATMs) {
        this.ATMs = ATMs;
    }

    public ArrayList<Long> getCenter() {
        return center;
    }

    public void setCenter(ArrayList<Long> center) {
        this.center = center;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getZoom_level() {
        return zoom_level;
    }

    public void setZoom_level(int zoom_level) {
        this.zoom_level = zoom_level;
    }
}
