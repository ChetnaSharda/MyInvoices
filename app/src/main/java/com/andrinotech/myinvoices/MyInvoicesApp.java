package com.andrinotech.myinvoices;

import android.app.Application;
import android.location.Location;

import com.andrinotech.myinvoices.Models.Invoices;
import com.andrinotech.myinvoices.helper.GPSTracker;

public class MyInvoicesApp extends Application {
    private static MyInvoicesApp instance;
    public static Invoices invoices = null;
    public static Location location = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static MyInvoicesApp getInstance() {
        return instance;
    }

}
