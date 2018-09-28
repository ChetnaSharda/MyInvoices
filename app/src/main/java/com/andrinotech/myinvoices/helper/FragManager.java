package com.andrinotech.myinvoices.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

public class FragManager {
    public static void replaceFragment(int id,FragmentManager fragmentManager, Fragment fragment, boolean addToStack, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(id, fragment, tag);

//        if (addToStack) {
//            transaction.addToBackStack(tag);
//        }
        transaction.commit();
    }

    public static void clearBackStack(FragmentManager fragmentManager) {

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
    }

}
