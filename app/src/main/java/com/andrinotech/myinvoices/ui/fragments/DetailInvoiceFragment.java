package com.andrinotech.myinvoices.ui.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrinotech.myinvoices.Controller.DetailInvoiceController;
import com.andrinotech.myinvoices.Models.Invoices;
import com.andrinotech.myinvoices.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Objects;

import static com.andrinotech.myinvoices.Controller.InvoiceController.currentImagefile;

public class DetailInvoiceFragment extends Fragment implements View.OnClickListener, DetailInvoiceController.InvoiceControllerRespnse {
    private ImageView image;
    private EditText ettitle, etcomment, etlocation, etshopname;
    private TextView etdate, takepicture;
    private View mView;
    private MaterialSpinner spin;
    Button delete, showmap;
    String spin_val = " ";
    DetailInvoiceController invoiceController;
    private Bitmap bitmap;
    private Uri inputImageUri1;
    private String json;
    private Invoices invoices;


    public static DetailInvoiceFragment newInstance(String json) {
        DetailInvoiceFragment fragment = new DetailInvoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("json", json);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_updateinvoice, container, false);
        initviews();
        return mView;
    }

    private void initviews() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            json = bundle.getString("json");
        }
        if (json == null) {
            getActivity().finish();
            return;
        }

        image = mView.findViewById(R.id.image);
        ettitle = mView.findViewById(R.id.ettitle);
        etcomment = mView.findViewById(R.id.etcomment);
        etdate = mView.findViewById(R.id.etdate);
        etlocation = mView.findViewById(R.id.etlocation);
        etshopname = mView.findViewById(R.id.etshopname);
        delete = mView.findViewById(R.id.delete);
        showmap = mView.findViewById(R.id.showmap);
        spin = (MaterialSpinner) mView.findViewById(R.id.type);//fetching view’s id
        delete.setOnClickListener(this);
        showmap.setOnClickListener(this);
        takepicture = mView.findViewById(R.id.takepicture);
        takepicture.setOnClickListener(this);
        etdate.setOnClickListener(this);
        invoiceController = new DetailInvoiceController(getActivity(), json, this);

        spin.setItems(invoiceController.getCategories());
        spin.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                spin_val = invoiceController.getCategories().get(position);//saving the value selected
            }
        });
        invoiceController.parseJson(json);
    }

    private void upload() {
        String date = etdate.getText().toString();
        String coment = etcomment.getText().toString();
        String shopname = etshopname.getText().toString();
        String title = ettitle.getText().toString();
        String location = etlocation.getText().toString();
        if (etdate.getText().toString().equalsIgnoreCase("")
                && etcomment.getText().toString().equalsIgnoreCase("")
                && etshopname.getText().toString().equalsIgnoreCase("")
                && ettitle.getText().toString().equalsIgnoreCase("")
                && etlocation.getText().toString().equalsIgnoreCase("")
                && currentImagefile == null && bitmap == null && spin_val.equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "please add one thing", Toast.LENGTH_SHORT).show();
        } else {
            invoiceController.update(bitmap, invoices.getId(), date, coment, shopname, title, location, inputImageUri1, spin_val);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                invoiceController.deleteInvoice(invoices.getId());
                break;
            case R.id.takepicture:
                invoiceController.askForDangerousPermissions(DetailInvoiceFragment.this);
                break;
            case R.id.etdate:
                invoiceController.pickdate(getActivity());
                break;
            case R.id.showmap:
                invoiceController.openMapsActivity(DetailInvoiceFragment.this, this.invoices);

//                reset();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invoiceController.onActivityResult(requestCode, resultCode, data);
    }


    public void startHomeActivity() {
        Objects.requireNonNull(getActivity()).finish();

    }

    @Override
    public void onSuccess() {
        startHomeActivity();
    }

    @Override
    public void getDate(String date) {
        etdate.setText(date);
    }

    @Override
    public void getInvoice(Invoices invoices) {
        this.invoices = invoices;
        etcomment.setText(invoices.getComment());
        etdate.setText(invoices.getDate());
        etlocation.setText(invoices.getLocation());
        etshopname.setText(invoices.getShopName());
        ettitle.setText(invoices.getTitle());
        if (invoices.getInvoiceType() != null) {
            spin_val = invoices.getInvoiceType();
            spin.setSelectedIndex(invoiceController.getCategoryIndex(spin_val));
        }
        if (invoices.getImage() != null) {
            bitmap = invoiceController.getImageDataInBitmap(invoices.getImage());
            image.setImageBitmap(bitmap);

        }
    }

    @Override
    public void locationError() {
        Toast.makeText(getActivity(), "Location not available for this invoice", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void getImageFromIntent(Uri uri) {
        try {
            inputImageUri1 = uri;
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), inputImageUri1);
            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        upload();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        invoiceController.onRequestPermissionsResult(DetailInvoiceFragment.this, requestCode, permissions, grantResults);
    }

}
