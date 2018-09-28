package com.andrinotech.myinvoices.ui.fragments;


import android.graphics.Bitmap;
import android.location.Location;
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

import com.andrinotech.myinvoices.Controller.InvoiceController;
import com.andrinotech.myinvoices.MyInvoicesApp;
import com.andrinotech.myinvoices.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Objects;

import static com.andrinotech.myinvoices.Controller.InvoiceController.currentImagefile;

public class NewInvoiceFragment extends Fragment implements View.OnClickListener, InvoiceController.InvoiceControllerRespnse {
    private ImageView image;
    private EditText ettitle, etcomment, etlocation, etshopname;
    private TextView etdate, takepicture;
    private View mView;
    private MaterialSpinner spin;
    Button reset, save;
    String spin_val = " ";
    InvoiceController invoiceController;
    private Bitmap bitmap;
    private Uri inputImageUri1;
    private boolean flag = false;


    public static NewInvoiceFragment newInstance() {
        NewInvoiceFragment fragment = new NewInvoiceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_addinvoice, container, false);
        initviews();
        return mView;
    }

    private void initviews() {

        image = mView.findViewById(R.id.image);
        ettitle = mView.findViewById(R.id.ettitle);
        etcomment = mView.findViewById(R.id.etcomment);
        etdate = mView.findViewById(R.id.etdate);
        etlocation = mView.findViewById(R.id.etlocation);
        etshopname = mView.findViewById(R.id.etshopname);
        save = mView.findViewById(R.id.save);
        reset = mView.findViewById(R.id.reset);
        spin = (MaterialSpinner) mView.findViewById(R.id.type);//fetching view’s id
        reset.setOnClickListener(this);
        save.setOnClickListener(this);
        takepicture = mView.findViewById(R.id.takepicture);
        takepicture.setOnClickListener(this);
        etdate.setOnClickListener(this);
        if (MyInvoicesApp.location != null) {
            etlocation.setText(MyInvoicesApp.location.getLatitude() + "," + MyInvoicesApp.location.getLongitude());
        } else {

        }
        invoiceController = new InvoiceController(getActivity(), this);
        spin.setItems(invoiceController.getCategories());
        spin.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                spin_val = invoiceController.getCategories().get(position);//saving the value selected
            }
        });

    }

    private void upload() {
        flag = true;

        String date = etdate.getText().toString();
        String coment = etcomment.getText().toString();
        String shopname = etshopname.getText().toString();
        String title = ettitle.getText().toString();
        String location = etlocation.getText().toString();
        if (MyInvoicesApp.location != null) {
            location = MyInvoicesApp.location.getLatitude() + "," + MyInvoicesApp.location.getLongitude();
        } else {
            if (invoiceController.getlocati() == null) {
                Toast.makeText(getActivity(), "Location Not found", Toast.LENGTH_SHORT).show();
            } else {
                location = invoiceController.getlocati().getLatitude() + "," + invoiceController.getlocati().getLongitude();
                MyInvoicesApp.location = invoiceController.getlocati();
            }
        }
        if (etdate.getText().toString().equalsIgnoreCase("")
                && etcomment.getText().toString().equalsIgnoreCase("")
                && etshopname.getText().toString().equalsIgnoreCase("")
                && ettitle.getText().toString().equalsIgnoreCase("")
                && location.equalsIgnoreCase("")
                && (currentImagefile == null || bitmap == null) && spin_val.equalsIgnoreCase(" ")) {
            Toast.makeText(getActivity(), "Add one thing to add the Invoice", Toast.LENGTH_SHORT).show();
        } else {
            invoiceController.Addinvoice(date, coment, shopname, title, location, inputImageUri1, spin_val);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                upload();
                break;
            case R.id.takepicture:
                invoiceController.askForDangerousPermissions(NewInvoiceFragment.this);
                break;
            case R.id.etdate:
                invoiceController.pickdate(getActivity());
                break;
            case R.id.reset:
                flag = true;
                reset();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invoiceController.onActivityResult(requestCode, resultCode, data);
    }

    public void reset() {
        Objects.requireNonNull(getActivity()).finish();

    }

    @Override
    public void onSuccess() {
        reset();
    }

    @Override
    public void getlocation(Location location) {
        if (location != null ) {
            String locationvlaue = location.getLatitude() + "," + location.getLongitude();
            etlocation.setText(locationvlaue);
        }

    }

    @Override
    public void getDate(String date) {
        etdate.setText(date);
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
        if (!flag) {
            upload();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        invoiceController.onRequestPermissionsResult(NewInvoiceFragment.this, requestCode, permissions, grantResults);
    }
}
