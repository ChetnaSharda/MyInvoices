package com.andrinotech.myinvoices.Controller;

import android.content.Context;

import com.andrinotech.myinvoices.Models.Invoices;
import com.andrinotech.myinvoices.database.DatabaseHandler;
import com.andrinotech.myinvoices.database.DatabaseManager;

import java.io.File;
import java.util.ArrayList;

public class ListInvoiceController {
    public static File currentImagefile;
    private final DatabaseHandler handler;
    private final DatabaseManager manager;
    private final Context app_context;
    public String mCurrentPhotoPath;
    public int CAMERA_PREVIEW_RESULT = 1;
    InvoiceControllerRespnse invoiceControllerRespnse;


    public ListInvoiceController(Context app_context, InvoiceControllerRespnse invoiceControllerRespnse) {
        this.app_context = app_context;
        this.invoiceControllerRespnse = invoiceControllerRespnse;
        handler = new DatabaseHandler(app_context);
        DatabaseManager.initializeInstance(new DatabaseHandler(app_context));
        manager = DatabaseManager.getInstance();

    }

    public void getInvoices() {
        ArrayList<Invoices> invoices = handler.getInvoices(handler, -1);
        if (invoices != null && invoices.size() > 0) {
            invoiceControllerRespnse.getInvoices(invoices);
        } else {
            invoiceControllerRespnse.setEmptyView();
        }
    }

    public interface InvoiceControllerRespnse {
        public void getInvoices(ArrayList<Invoices> invoices);

        public void setEmptyView();
    }
}
