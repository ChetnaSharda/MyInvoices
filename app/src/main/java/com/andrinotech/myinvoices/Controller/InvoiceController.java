package com.andrinotech.myinvoices.Controller;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.andrinotech.myinvoices.helper.DynamicPermission;
import com.andrinotech.myinvoices.helper.FileHelper;
import com.andrinotech.myinvoices.helper.GPSTracker;
import com.andrinotech.myinvoices.Models.Invoices;
import com.andrinotech.myinvoices.MyInvoicesApp;
import com.andrinotech.myinvoices.helper.Locator;
import com.andrinotech.myinvoices.ui.fragments.NewInvoiceFragment;
import com.andrinotech.myinvoices.helper.Utilforbitmap;
import com.andrinotech.myinvoices.database.DatabaseHandler;
import com.andrinotech.myinvoices.database.DatabaseManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class InvoiceController implements GPSTracker.LocationChangeListner, Locator.Listener {
    public static File currentImagefile;
    private final DatabaseHandler handler;
    private final DatabaseManager manager;
    private final Context app_context;
    //    private final GPSTracker gpsTracker;
    private final Locator locator;
    public String mCurrentPhotoPath;
    public int CAMERA_PREVIEW_RESULT = 1;
    InvoiceControllerRespnse invoiceControllerRespnse;
    Calendar mBirthdate;
    int day, month, year;

    public ArrayList<String> getCategories() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Clothes");
        strings.add("Home");
        strings.add("Stationery");
        strings.add("Electronics");


        return strings;
    }

    public InvoiceController(Context app_context, InvoiceControllerRespnse invoiceControllerRespnse) {
        this.app_context = app_context;
        this.invoiceControllerRespnse = invoiceControllerRespnse;
        handler = new DatabaseHandler(app_context);
        DatabaseManager.initializeInstance(new DatabaseHandler(app_context));
        manager = DatabaseManager.getInstance();
//        gpsTracker = new GPSTracker(app_context, this);
        locator = new Locator(app_context);
        locator.getLocation(app_context, Locator.Method.NETWORK, this);

    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!storageDir.exists()) {
            if (!storageDir.mkdir()) {
                Log.e("TAG", "Throwing Errors....");
                throw new IOException();
            }
        }
        currentImagefile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = currentImagefile.getAbsolutePath();
        return currentImagefile;
    }

    public Intent dispatchTakePictureIntent(NewInvoiceFragment context) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getActivity().getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File newFile = null;
                try {
                    newFile = createImageFile();
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Uri photoURI = FileProvider.getUriForFile(context.getActivity(), "com.andrinotech.myinvoices.fileprovider", newFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        if (takePictureIntent != null)
            context.startActivityForResult(takePictureIntent, CAMERA_PREVIEW_RESULT);

        return null;
    }

    public void Addinvoice(String date, String coment, String shopname, String title, String location, Uri mCurrentPhotoPath, String spinval) {
        Bitmap bitmap = null;
        byte[] byteArray = null;
        try {
            if (mCurrentPhotoPath != null) {
                bitmap = MediaStore.Images.Media.getBitmap(app_context.getContentResolver(), mCurrentPhotoPath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            handler.insertinvoices(handler, new Invoices(-1, title, date, spinval, shopname, location, coment, byteArray));
            invoiceControllerRespnse.onSuccess();
            Intent intent = new Intent("updatelist");
            LocalBroadcastManager.getInstance(MyInvoicesApp.getInstance().getApplicationContext()).sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pickdate(Context context) {
        mBirthdate = Calendar.getInstance();
        day = mBirthdate.get(Calendar.DAY_OF_MONTH);
        month = mBirthdate.get(Calendar.MONTH);
        year = mBirthdate.get(Calendar.YEAR);
        month = month;
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1 + 1;
                invoiceControllerRespnse.getDate("" + i2 + "/" + i1 + "/" + i);

            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri inputImageUri1 = Uri.fromFile(currentImagefile);
                    try {
                        File p = Utilforbitmap.getCompressed(app_context, inputImageUri1.getPath());
                        if (p != null) {
                            Uri temp = FileProvider.getUriForFile(app_context, "com.andrinotech.myinvoices.fileprovider", p);
                            if (temp != null) {
                                invoiceControllerRespnse.getImageFromIntent(temp);

                            } else {
                                invoiceControllerRespnse.getImageFromIntent(inputImageUri1);

                            }
                        } else {
                            invoiceControllerRespnse.getImageFromIntent(inputImageUri1);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                default:
            }

        }
    }

    public static Uri getCompressUri(Context context, Uri uri) {
        Bitmap temp = null;

        String imagebase64String = null;
        try {
            temp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            if (temp != null) {
                File compressedImage = new Compressor(context)
                        .setMaxWidth(300)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .compressToFile(FileHelper.from(context, uri));
                if (compressedImage != null) {
                    Uri compressuri = Uri.fromFile(compressedImage);
                    return compressuri;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void getlocation(Location location) {
        if (invoiceControllerRespnse != null) {
            invoiceControllerRespnse.getlocation(location);
        }
    }

    public Location getlocati() {
//        return gpsTracker.getLocation();
        return locator.getLocation();
    }

    @Override
    public void onLocationFound(Location location) {
        if (invoiceControllerRespnse != null) {
            invoiceControllerRespnse.getlocation(location);
        }

    }

    @Override
    public void onLocationNotFound() {

    }

    public interface InvoiceControllerRespnse {
        public void onSuccess();

        public void getlocation(Location location);

        public void getDate(String date);

        public void getImageFromIntent(Uri uri);
    }

    public void askForDangerousPermissions(NewInvoiceFragment context) {
        ArrayList<String> permissions = new ArrayList<String>();

        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        DynamicPermission dynamicPermission = new DynamicPermission(context.getActivity(), permissions);
        boolean flag = dynamicPermission.checkFragmentAndRequestPermissions(context);

        if (flag) {
            dispatchTakePictureIntent(context);
        }
    }

    public void onRequestPermissionsResult(NewInvoiceFragment context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                app_context);
        builder.setTitle("Need Storage Permission");
        builder.setMessage("This app needs storage permission.");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("WrongConstant")
                    public void run() {
                        Toast.makeText(MyInvoicesApp.getInstance().getApplicationContext(), "This app needs storage permission", Toast.LENGTH_SHORT).show();
                    }
                });

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
                        showRationale = context.shouldShowRequestPermissionRationale(permission);
                    }
                    if (!showRationale) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getActivity().getPackageName(), null);
                        intent.setData(uri);
                        context.startActivityForResult(intent, 1);
                        isgranted = false;
                        break;
                    } else {
                        builder.show();
                    }

                    isgranted = false;
                    break;
                }
            }
            if (!isgranted) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("WrongConstant")
                    public void run() {
                        Toast.makeText(MyInvoicesApp.getInstance().getApplicationContext(), "This app needs storage permission", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dispatchTakePictureIntent(context);
            }


        }
    }

}
