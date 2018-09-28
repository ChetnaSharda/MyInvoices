package com.andrinotech.myinvoices.helper;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.andrinotech.myinvoices.R;


public class AVProgressDialog extends Dialog {

    public AVProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        setTitle("");
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setMessage(CharSequence message) {
        TextView textView = ((TextView) findViewById(R.id.message));
        textView.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.message)).setText(message);
    }

}
