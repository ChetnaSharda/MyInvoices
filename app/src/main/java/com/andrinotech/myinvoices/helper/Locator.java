package com.andrinotech.myinvoices.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class Locator implements LocationListener {

    static private final String LOG_TAG = "locator";

    static private final int TIME_INTERVAL = 100; // minimum time between updates in milliseconds
    static private final int DISTANCE_INTERVAL = 1; // minimum distance between updates in meters

    static public enum Method {
        NETWORK,
        GPS,
        NETWORK_THEN_GPS
    }

    private Context context;
    private LocationManager locationManager;
    private Locator.Method method;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;
    private Locator.Listener callback;

    public Locator(Context context) {
        super();
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getLocation(Context context, Locator.Method method, Locator.Listener callback) {
        this.method = method;
        this.callback = callback;
        switch (this.method) {
            case NETWORK:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location networkLocation = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (networkLocation != null) {
                    location=networkLocation;
                    Log.d(LOG_TAG, "Last known location found for network provider : " + networkLocation.toString());
                    this.callback.onLocationFound(networkLocation);
                } else {
                    Log.d(LOG_TAG, "Request updates from network provider.");
                    this.requestUpdates(LocationManager.NETWORK_PROVIDER);
                }
                break;
            case GPS:
                Location gpsLocation = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    location=gpsLocation;

                    Log.d(LOG_TAG, "Last known location found for GPS provider : " + gpsLocation.toString());
                    this.callback.onLocationFound(gpsLocation);
                } else {
                    Log.d(LOG_TAG, "Request updates from GPS provider.");
                    this.requestUpdates(LocationManager.GPS_PROVIDER);
                }
                break;
        }
    }

    private void requestUpdates(String provider) {
        if (this.locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER)
                    && Connectivity.isConnected(this.context)) {
                Log.d(LOG_TAG, "Network connected, start listening : " + provider);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                this.locationManager.requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER)
                    && Connectivity.isConnectedMobile(this.context)) {
                Log.d(LOG_TAG, "Mobile network connected, start listening : " + provider);
                this.locationManager.requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else {
                Log.d(LOG_TAG, "Proper network not connected for provider : " + provider);
                this.onProviderDisabled(provider);
            }
        } else {
            this.onProviderDisabled(provider);
        }
    }

    public void cancel() {
        Log.d(LOG_TAG, "Locating canceled.");
        this.locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "Location found : " + location.getLatitude() + ", " + location.getLongitude() + (location.hasAccuracy() ? " : +- " + location.getAccuracy() + " meters" : ""));
        this.locationManager.removeUpdates(this);
        this.callback.onLocationFound(location);
        this.location=location;
    }


    @Override
    public void onProviderDisabled(String provider) {
        Log.d(LOG_TAG, "Provider disabled : " + provider);
        if (this.method == Locator.Method.NETWORK_THEN_GPS
                && provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
            // Network provider disabled, try GPS
            Log.d(LOG_TAG, "Requesst updates from GPS provider, network provider disabled.");
            this.requestUpdates(LocationManager.GPS_PROVIDER);
        } else {
            this.locationManager.removeUpdates(this);
            this.callback.onLocationNotFound();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG, "Provider enabled : " + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(LOG_TAG, "Provided status changed : " + provider + " : status : " + status);
    }

    public interface Listener {
        void onLocationFound(Location location);

        void onLocationNotFound();
    }

}