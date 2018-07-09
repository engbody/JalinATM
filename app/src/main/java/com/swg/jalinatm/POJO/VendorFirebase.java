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

    private ArrayList<Long> center;
    private ArrayList<String> ATMs;
    private String name;
    private String email;
    private Long atm_id;
    private Long status;
    private Long timestamp;
    private Long zoom_level;
    private String key;

    public VendorFirebase(ArrayList<Long> center, String name, Long status, Long timestamp, Long zoom_level) {
        this.center = center;
        this.name = name;
        this.status = status;
        this.timestamp = timestamp;
        this.zoom_level = zoom_level;
    }

    public VendorFirebase() {
    }

    public ArrayList<String> getATMs() {
        return ATMs;
    }

    public void setATMs(ArrayList<String> ATMs) {
        this.ATMs = ATMs;
    }

    public Long getAtm_id() {
        return atm_id;
    }

    public void setAtm_id(Long atm_id) {
        this.atm_id = atm_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getZoom_level() {
        return zoom_level;
    }

    public void setZoom_level(Long zoom_level) {
        this.zoom_level = zoom_level;
    }
}
