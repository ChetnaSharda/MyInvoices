package com.andrinotech.myinvoices.helper;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;

// Utill class  containing all functions nedded for bitmap

public class Utilforbitmap {
    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws
            URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public static String getNameFromUri(Context mContext, Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(0);
                    }

                } catch (Exception e) {

                }
            } else {
                fileName = uri.getLastPathSegment();
            }
        } catch (SecurityException e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }


    public static boolean checkStorage() {
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        switch (state) {
            case Environment.MEDIA_MOUNTED:
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
                break;
            default:
                // Something else is wrong. It may be one of many other states, but
                // all we need
                // to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
                break;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }



    public static File createNewAudioFile(String extension) {
        File audiopath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "hub");
        if (!audiopath.exists()) {
            if (!audiopath.mkdirs()) {
                return null;
            } else {
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File audio = new File(audiopath, "hub_" + timeStamp + extension);
        if (!audio.exists()) {
            try {
                audio.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return audio;
    }
    public static File createNewAttachmentFile(Context mContext, String extension) {
        File f = null;
        if (checkStorage()) {
            f = getImageFile();
        }
        return f;
    }

    public static void createAttachmentFromUri(Context mContext, Uri uri, boolean flag) {
        createAttachmentFromUri(mContext, uri, false, flag);

    }


    /**
     * Creates a fiile to be used as attachment.
     */
    public static void createAttachmentFromUri(Context mContext, Uri uri, boolean moveSource, boolean flag) {
        String name = FileHelper.getNameFromUri(mContext, uri);
        String extension = FileHelper.getFileExtension(FileHelper.getNameFromUri(mContext, uri)).toLowerCase(
                Locale.getDefault());
        File f = createExternalStoragePrivateFile(mContext, uri, extension, flag);


    }

    public static File createNewAttachmentFileAUDI(Context mContext, String extension) {
        File f = null;
        if (checkStorage()) {
            f = new File(mContext.getExternalFilesDir(null), createNewAttachmentName(extension));
        }
        return f;
    }


    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static synchronized String createNewAttachmentName(String extension) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String name = "newletschat";
        name += extension != null ? extension : "";
        return name;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        if (uri == null)
            return null;

        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];
            MediaStoreFactory mediaStoreFactory = new MediaStoreFactory();
            Uri contentUri = mediaStoreFactory.createURI(type);

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{split[1]};

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static File createExternalStoragePrivateFile(Context mContext, Uri uri, String extension, boolean flag) {

        // Checks for external storage availability
        if (!checkStorage()) {
            Toast.makeText(mContext, "not avaia", Toast.LENGTH_SHORT).show();
            return null;
        }
        File file;
//        if(flag) {
//             file = createNewAttachmentFile(mContext, extension);
//        }else {
        file = getImageFile();

//        }
        InputStream is = null;
        OutputStream os = null;
        try {
            is = mContext.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(file);
            copyFile(is, os);
        } catch (IOException e) {
            try {
                is = new FileInputStream(getPath(mContext, uri));
                os = new FileOutputStream(file);
                copyFile(is, os);
                // It's a path!!
            } catch (NullPointerException e1) {
                try {
                    is = new FileInputStream(uri.getPath());
                    os = new FileOutputStream(file);
                    copyFile(is, os);
                } catch (FileNotFoundException e2) {
                    file = null;
                }
            } catch (FileNotFoundException e2) {
                file = null;
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {

            }

        }
        return file;
    }


    /**
     * Generic file copy method
     *
     * @param is Input
     * @param os Output
     * @return True if copy is done, false otherwise
     */
    public static boolean copyFile(InputStream is, OutputStream os) {
        boolean res = false;
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = is.read(data)) > 0) {
                os.write(data, 0, len);
            }
            is.close();
            os.close();
            res = true;
        } catch (IOException e) {

        }
        return res;
    }

    public static String getMimeType(Context mContext, Uri uri) {
        ContentResolver cR = mContext.getContentResolver();
        String mimeType = cR.getType(uri);
        if (mimeType == null) {
            mimeType = getMimeType(uri.toString());
        }
        return mimeType;
    }


    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static Uri getImageFileURI(Context mContext,File file) {
//    return
        return FileProvider.getUriForFile(
                mContext,
                mContext.getApplicationContext()
                        .getPackageName() + ".provider", file);
    }

    public static File getImageFile() {
        File imagePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "hub");
        if (!imagePath.exists()) {
            if (!imagePath.mkdirs()) {
                return null;
            } else {
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File image = new File(imagePath, "hub_" + timeStamp + ".jpg");
        if (!image.exists()) {
            try {
                image.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return image;
    }


    public static Bitmap getThumbnail(Context mContext, Uri uri, int reqWidth, int reqHeight) {
        Bitmap srcBmp = decodeSampledFromUri(mContext, uri, reqWidth, reqHeight);
        Bitmap dstBmp = null;
        if (srcBmp != null) {
            if (srcBmp.getWidth() < reqWidth && srcBmp.getHeight() < reqHeight) {
                dstBmp = ThumbnailUtils.extractThumbnail(srcBmp, reqWidth, reqHeight);
            } else {
                int x = 0;
                int y = 0;
                int width = srcBmp.getWidth();
                int height = srcBmp.getHeight();
                float ratio = (float) reqWidth / (float) reqHeight * ((float) srcBmp.getHeight() / (float) srcBmp.getWidth());
                if (ratio < 1.0F) {
                    x = (int) ((float) srcBmp.getWidth() - (float) srcBmp.getWidth() * ratio) / 2;
                    width = (int) ((float) srcBmp.getWidth() * ratio);
                } else {
                    y = (int) ((float) srcBmp.getHeight() - (float) srcBmp.getHeight() / ratio) / 2;
                    height = (int) ((float) srcBmp.getHeight() / ratio);
                }

                int rotation = neededRotation(new File(uri.getPath()));
                if (rotation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) rotation);
                    dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height, matrix, true);
                } else {
                    dstBmp = Bitmap.createBitmap(srcBmp, x, y, width, height);
                }
            }
        }
        return dstBmp;
    }

    public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Uri getCompressUri(Context context, Uri uri) {
        Bitmap temp = null;

        String imagebase64String = null;
        try {
            temp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            if (temp != null) {
                File compressedImage = new Compressor(context)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(70)
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




    public static int neededRotation(File ff) {
        try {
            ExifInterface e = new ExifInterface(ff.getAbsolutePath());
            int orientation = e.getAttributeInt("Orientation", 1);
            return orientation == 8 ? 270 : (orientation == 3 ? 180 : (orientation == 6 ? 90 : 0));
        } catch (IOException var3) {
            var3.printStackTrace();
            return 0;
        }
    }

    public static Bitmap decodeSampledFromUri(Context mContext, Uri uri, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream inputStream = null;
        InputStream inputStreamSampled = null;

        Bitmap e1;
        try {
            inputStream = mContext.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(inputStream, (Rect) null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            inputStreamSampled = mContext.getContentResolver().openInputStream(uri);
            Bitmap e = BitmapFactory.decodeStream(inputStreamSampled, (Rect) null, options);
            return e;
        } catch (IOException var18) {
            Log.e("BitmapUtils", "Error");
            e1 = BitmapFactory.decodeResource(mContext.getResources(), android.support.design.R.drawable.abc_ab_share_pack_mtrl_alpha);
        } finally {
            try {
                inputStream.close();
                inputStreamSampled.close();
            } catch (NullPointerException | IOException var17) {
                Log.e("BitmapUtils", "Failed to close streams");
            }

        }

        return e1;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) throws FileNotFoundException {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;

            int halfWidth;
            for (halfWidth = width / 2; halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth; inSampleSize *= 2) {

            }

            while (halfHeight / inSampleSize > reqHeight * 2 || halfWidth / inSampleSize > reqWidth * 2) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyymmddhhmmss", Locale.getDefault());

    public static File getCompressed(Context context, String path) throws IOException {

        if(context == null)
            throw new NullPointerException("Context must not be null.");
        //getting device external cache directory, might not be available on some devices,
        // so our code fall back to internal storage cache directory, which is always available but in smaller quantity
        File cacheDir = context.getExternalCacheDir();
        if(cacheDir == null)
            //fall back
            cacheDir = context.getCacheDir();

        String rootDir = cacheDir.getAbsolutePath() + "/ImageCompressor";
        File root = new File(rootDir);

        //Create ImageCompressor folder if it doesnt already exists.
        if(!root.exists())
            root.mkdirs();

        //decode and resize the original bitmap from @param path.
        Bitmap bitmap = decodeImageFromFiles(path, /* your desired width*/300, /*your desired height*/ 300);

        //create placeholder for the compressed image file
        File compressed = new File(root, SDF.format(new Date()) + ".jpg" /*Your desired format*/);

        //convert the decoded bitmap to stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        /*compress bitmap into byteArrayOutputStream
            Bitmap.compress(Format, Quality, OutputStream)
            Where Quality ranges from 1 - 100.
         */
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        /*
        Right now, we have our bitmap inside byteArrayOutputStream Object, all we need next is to write it to the compressed file we created earlier,
        java.io.FileOutputStream can help us do just That!
         */
        FileOutputStream fileOutputStream = new FileOutputStream(compressed);
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.flush();

        fileOutputStream.close();

        //File written, return to the caller. Done!
        return compressed;
    }

    public static Bitmap decodeImageFromFiles(String path, int width, int height) {
        BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
        scaleOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, scaleOptions);
        int scale = 1;
        while (scaleOptions.outWidth / scale / 2 >= width
                && scaleOptions.outHeight / scale / 2 >= height) {
            scale *= 2;
        }
        // decode with the sample size
        BitmapFactory.Options outOptions = new BitmapFactory.Options();
        outOptions.inSampleSize = scale;
        return BitmapFactory.decodeFile(path, outOptions);
    }
}

