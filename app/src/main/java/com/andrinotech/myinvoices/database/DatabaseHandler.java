package com.andrinotech.myinvoices.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.andrinotech.myinvoices.Models.Invoices;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    //Database Version
    private static final int database_version = 1;
    //DataBase Name
    private static final String DATABASE_NAME = "myinvoices";

    //Table Name
    private static final String Invoice = "invoice";

    private String CREATE_INVOICES = "CREATE TABLE IF NOT EXISTS " + Invoice + " " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title text, " +
            "date text, " +
            "invoiceType text, " +
            "shopname text, " +
            "location text, " +
            "comment text, " +
            "imagedata BLOB)";


    //***********************
    //CONSTRUCTOR
    //***********************
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, database_version);

    }

    //************************
    //INHERITED METHODS
    //************************


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_INVOICES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Invoice);
        db.execSQL(CREATE_INVOICES);
        Log.d("updatetable", "asdasd");
    }


    public void updateInvoice(DatabaseHandler db, Invoices data) {
        SQLiteDatabase sql = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", data.getTitle());
        contentValues.put("date", data.getDate());

        contentValues.put("invoiceType", data.getInvoiceType());
        contentValues.put("shopname", data.getShopName());
        contentValues.put("location", data.getLocation());
        contentValues.put("comment", data.getComment());

        contentValues.put("imagedata", data.getImage());
        sql.update(Invoice, contentValues, "id='" + data.getId() + "'", null);

    }

    //SubscriberPayment

    public void insertinvoices(DatabaseHandler db, Invoices data) {
        SQLiteDatabase sql = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", data.getTitle());
        contentValues.put("date", data.getDate());

        contentValues.put("invoiceType", data.getInvoiceType());
        contentValues.put("shopname", data.getShopName());
        contentValues.put("location", data.getLocation());
        contentValues.put("comment", data.getComment());

        contentValues.put("imagedata", data.getImage());
        sql.insertOrThrow(Invoice, null, contentValues);
    }

    public void deleteSubscriberPaymentSummary(DatabaseHandler db) {
        SQLiteDatabase sql = db.getWritableDatabase();
        sql.execSQL("DELETE FROM " + Invoice + "");
    }


    public ArrayList<Invoices> getInvoices(DatabaseHandler db, int invoiceid) {

        ArrayList<Invoices> arrayList = new ArrayList<>();
        try {
            SQLiteDatabase sql = db.getReadableDatabase();
            String strsql = "SELECT * From " + Invoice;
            Cursor cursor = sql.rawQuery(strsql, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int ID = cursor.getInt(cursor.getColumnIndex("id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String date = cursor.getString(cursor.getColumnIndex("date"));
                    String invoiceType = cursor.getString(cursor.getColumnIndex("invoiceType"));
                    String shopename = cursor.getString(cursor.getColumnIndex("shopname"));
                    String location = cursor.getString(cursor.getColumnIndex("location"));
                    String comment = cursor.getString(cursor.getColumnIndex("comment"));
                    byte[] imagedata = cursor.getBlob(cursor.getColumnIndex("imagedata"));
                    arrayList.add(new Invoices(ID, title, date, invoiceType, shopename, location, comment, imagedata));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;

    }

    public void deleteInvoice(DatabaseHandler db, String id) {
        SQLiteDatabase sql = db.getWritableDatabase();
        sql.delete(Invoice, " id = ?", new String[]{id});
    }

}