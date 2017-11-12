package com.goodpaths.goodpaths.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

/**
 * Created by fma on 11/11/17.
 */

public class InfoOverlay {

    public static void displayOverlay(Context context){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("About")
                .setMessage("Welcome to GoodPath! This app is here to help you and other people find safer and easier ways to get around your town when walking alone or in small groups. If you ever feel unsafe in a place, you can mark it as unsafe using the bottom right button. You can also mark places that are hard to access for people with reduced mobility. You can switch between those two modes using ")
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }





}
