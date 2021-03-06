package com.swg.jalinatm.POJO;

import android.location.Location;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by Andrew Widjaja on 5/28/2018.
 */

@Parcel
public class ATM {

    private String id;
    private String description;
    private String address;
    private LatLng loc;

    public ATM(){}

    public ATM(String id, String description, String address, LatLng loc) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.loc = loc;
    }

//    protected ATM(android.os.Parcel in) {
//        id = in.readString();
//        description = in.readString();
//        address = in.readString();
//        loc = new LatLng(in.readDouble(), in.readDouble());
//    }

//    public static final Creator<ATM> CREATOR = new Creator<ATM>() {
//        @Override
//        public ATM createFromParcel(android.os.Parcel in) {
//            return new ATM(in);
//        }
//
//        @Override
//        public ATM[] newArray(int size) {
//            return new ATM[size];
//        }
//    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(android.os.Parcel dest, int flags) {
//        dest.writeString(id);
//        dest.writeString(description);
//        dest.writeString(address);
//        dest.writeDouble(loc.latitude);
//        dest.writeDouble(loc.longitude);
//    }
}
