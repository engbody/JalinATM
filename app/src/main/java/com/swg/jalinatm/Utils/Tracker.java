package com.swg.jalinatm.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swg.jalinatm.POJO.Vendor;

public class Tracker implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Tracker";
    private static final int LOCATION_REQUEST = 2;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    private LocationRequest locationRequest;
    private GoogleApiClient googleClient;
    private Location location;
    private PendingResult<Status> pendingResult;
    private Context context;
    private Vendor vendor;

    private DatabaseReference database;
    private GeoFire geoFire;

    private String userKey;

    private void requestLocation() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(INTERVAL);
        this.locationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public Tracker() {
    }

    public Tracker(Context context, Vendor vendor) {
        this.context = context;
        this.vendor = vendor;
        this.database = FirebaseDatabase.getInstance().getReference().child("location");
        this.geoFire = new GeoFire(database);
        requestLocation();
        this.googleClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(this.googleClient, this.locationRequest, this);
        Log.i(TAG, "Location update started");
    }

    public void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleClient, this);
        Log.i(TAG, "Location update stopped");
    }

    public void connectGoogleApi() {
        googleClient.connect();
    }

    public void disconnectGoogleApi() {
        googleClient.disconnect();
    }

    public boolean isGoogleApiConnected() {
        return googleClient != null && googleClient.isConnected();
    }

    public Vendor getVendor(){
        return this.vendor;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location changed");
        if(vendor!=null){
            if(this.location!=null) {
                vendor.setPrevLoc(new LatLng(this.location.getLatitude(), this.location.getLongitude()));
                vendor.setLoc(new LatLng(location.getLatitude(), location.getLongitude()));
            } else {
                this.location = location;
                vendor.setLoc(new LatLng(this.location.getLatitude(), this.location.getLongitude()));
            }
            vendor.setLastUpdateTime(System.currentTimeMillis());
        }
        if(userKey!=null){
            this.geoFire.setLocation("1" + userKey, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error != null) {
                        Log.e(TAG,"There was an error saving the location to GeoFire: " + error);
                    } else {
                        Log.i(TAG,"Location saved on server successfully!");
                    }
                }
            });
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient Connected");
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
