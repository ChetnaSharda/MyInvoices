package com.andrinotech.myinvoices.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.andrinotech.myinvoices.ui.fragments.DetailInvoiceFragment;
import com.andrinotech.myinvoices.helper.FragManager;
import com.andrinotech.myinvoices.ui.fragments.NewInvoiceFragment;
import com.andrinotech.myinvoices.R;

public class InvoiceActivtiy extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        mFragmentManager = getSupportFragmentManager();
        initiLizeFragment();

    }

    private void initiLizeFragment() {
        if (getIntent() != null) {
            if (getIntent().getStringExtra("invoices") != null) {
                String json = getIntent().getStringExtra("invoices");
                FragManager.replaceFragment(R.id.flContainerFragment, mFragmentManager, DetailInvoiceFragment.newInstance(json), true, "newInvoiceFragment");
                getSupportActionBar().setTitle("Detail Invoice");
            } else {
                FragManager.replaceFragment(R.id.flContainerFragment, mFragmentManager, NewInvoiceFragment.newInstance(), true, "newInvoiceFragment");
                getSupportActionBar().setTitle("Add NewInvoice");

            }
        } else {
            getSupportActionBar().setTitle("Add NewInvoice");

            FragManager.replaceFragment(R.id.flContainerFragment, mFragmentManager, NewInvoiceFragment.newInstance(), true, "newInvoiceFragment");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
