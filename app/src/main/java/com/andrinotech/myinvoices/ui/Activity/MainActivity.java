package com.andrinotech.myinvoices.ui.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.andrinotech.myinvoices.MyInvoicesApp;
import com.andrinotech.myinvoices.helper.DynamicPermission;
import com.andrinotech.myinvoices.helper.FragManager;
import com.andrinotech.myinvoices.helper.GPSTracker;
import com.andrinotech.myinvoices.ui.fragments.ListInvoiceFragment;
import com.andrinotech.myinvoices.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForDangerousPermissions();

        mFragmentManager = getSupportFragmentManager();
        FragManager.replaceFragment(R.id.flContainerFragmentt, mFragmentManager, ListInvoiceFragment.newInstance(), true, "newInvoiceFragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.newInvoice) {
            startActivity(new Intent(this, InvoiceActivtiy.class));
            return true;
        } else if (itemId == R.id.help) {
            startActivity(new Intent(this, WebViewActivtiy.class));

        }
        return super.onOptionsItemSelected(item);
    }

    private void askForDangerousPermissions() {
        ArrayList<String> permissions = new ArrayList<String>();

        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        DynamicPermission dynamicPermission = new DynamicPermission(this, permissions);
        boolean flag = dynamicPermission.checkAndRequestPermissions();

        if (flag) {
            GPSTracker gpsTracker = new GPSTracker(this);
            gpsTracker.getLocation(true);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                this);
        builder.setTitle("Requested Permission Required!s");
        builder.setMessage("This app needs requested permissions.");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
                gpsTracker.getLocation(true);
                dialog.dismiss();
            }
        });
        if (requestCode == 11) {
            boolean isgranted = true;

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    boolean showRationale = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRationale = shouldShowRequestPermissionRationale(permission);
                    }
                    if (!showRationale) {
                        isgranted = false;

                    } else {
                        builder.show();
                        isgranted = false;

                    }

                }
            }
            if (!isgranted) {
            } else {
                GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
                gpsTracker.getLocation(true);
            }

        }
    }

}
