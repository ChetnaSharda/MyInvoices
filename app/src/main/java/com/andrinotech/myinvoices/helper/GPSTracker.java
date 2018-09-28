package com.andrinotech.myinvoices.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.andrinotech.myinvoices.MyInvoicesApp;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GPSTracker extends Service implements LocationListener {

    private final Context context;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    double latitude;
    double longitude;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    Location location;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 100;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;


    protected LocationManager locationManager;

    public interface LocationChangeListner {
        public void getlocation(Location location);
    }

    LocationChangeListner locationChangeListner;

    public GPSTracker(Context context, LocationChangeListner locationListner) {
        this.context = context;
        this.locationChangeListner = locationListner;
        getLocation(true);
    }

    public GPSTracker(Context context) {
        this.context = context;
        getLocation(true);
    }

    public Location getLocation(boolean permission) {

        try {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                if (permission) {
                    LocationRequest mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10 * 1000)
                            .setFastestInterval(1 * 1000);
                    LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(mLocationRequest);
                    settingsBuilder.setAlwaysShow(true);
                    Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context)
                            .checkLocationSettings(settingsBuilder.build());
                    result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                            try {
                                LocationSettingsResponse response =
                                        task.getResult(ApiException.class);
//                            getLocation();
                            } catch (ApiException ex) {
                                switch (ex.getStatusCode()) {
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            ResolvableApiException resolvableApiException =
                                                    (ResolvableApiException) ex;
                                            resolvableApiException
                                                    .startResolutionForResult((Activity) context,
                                                            1);
                                        } catch (IntentSender.SendIntentException e) {

                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                        break;
                                }
                            }
                        }
                    });
                }else {
//                    showSettingAlert();
                }
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            this);


                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            if (location != null) {
                                MyInvoicesApp.location = location;
                            }
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }

                    }

                }

                if (isGPSEnabled) {

                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    this);


                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

                                if (location != null) {
                                    if (location != null) {
                                        MyInvoicesApp.location = location;
                                    }
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }

                            }

                        }
                    }

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;


    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(GPSTracker.this);
            }
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }


    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingAlert() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("GPS Settings");

        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
//
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });

        alertDialog.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        getLatitude();
        getLongitude();
        if (location != null && locationChangeListner != null) {
            locationChangeListner.getlocation(location);
        }
        if (location != null) {
            MyInvoicesApp.location = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

}