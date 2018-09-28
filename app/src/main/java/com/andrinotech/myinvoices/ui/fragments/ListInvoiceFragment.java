package com.andrinotech.myinvoices.ui.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrinotech.myinvoices.helper.AVProgressDialog;
import com.andrinotech.myinvoices.Controller.ListInvoiceController;
import com.andrinotech.myinvoices.ui.adpater.InvoiceListAdapter;
import com.andrinotech.myinvoices.Models.Invoices;
import com.andrinotech.myinvoices.MyInvoicesApp;
import com.andrinotech.myinvoices.R;
import com.andrinotech.myinvoices.helper.RecyclerViewListItemDecorator;
import com.andrinotech.myinvoices.ui.Activity.InvoiceActivtiy;

import java.util.ArrayList;

public class ListInvoiceFragment extends Fragment implements ListInvoiceController.InvoiceControllerRespnse, InvoiceListAdapter.OnItemClickListener {
    private View mView;
    private RecyclerView recyclerView;
    private InvoiceListAdapter adapter;
    private ListInvoiceController invoiceController;
    private TextView emptyview;
    private ArrayList<Invoices> invoices = new ArrayList<>();
    private AVProgressDialog mLoadingDialog;

    public static ListInvoiceFragment newInstance() {
        ListInvoiceFragment fragment = new ListInvoiceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_list_invoice, container, false);
        initviews();
        return mView;
    }

    BroadcastReceiver updaetList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invoiceController.getInvoices();
        }
    };


    private void initviews() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.invoices_list);
        emptyview = mView.findViewById(R.id.emptyview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerViewListItemDecorator(getActivity(), null));
        adapter = new InvoiceListAdapter(new ArrayList<Invoices>(), getActivity());
        adapter.SetOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        invoiceController = new ListInvoiceController(getActivity(), this);
        LocalBroadcastManager.getInstance(MyInvoicesApp.getInstance().getApplicationContext()).registerReceiver(updaetList, new IntentFilter("updatelist"));
        mLoadingDialog = new AVProgressDialog(getActivity());

    }


    @Override
    public void getInvoices(ArrayList<Invoices> invoices) {
        emptyview.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        this.invoices = invoices;
        adapter.setItemList(invoices);
        dismissDialog();
    }

    @Override
    public void setEmptyView() {
        emptyview.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        dismissDialog();

    }

    @Override
    public void onResume() {
        super.onResume();
//        mLoadingDialog.show();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                invoiceController.getInvoices();
//
//            }
//        }, 500);
    }

    private void dismissDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
    }

    @Override
    public void onItemClick(View view, int pos) {
        Invoices invoices = this.invoices.get(pos);
        MyInvoicesApp.invoices = invoices;
        Intent intent = new Intent(getActivity(), InvoiceActivtiy.class);
        intent.putExtra("invoices", "asd");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        MyInvoicesApp.getInstance().getApplicationContext().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MyInvoicesApp.getInstance().getApplicationContext()).unregisterReceiver(updaetList);

    }
}