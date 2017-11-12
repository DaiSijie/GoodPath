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
                .setMessage("Whenever you feel unsafe, just tap on the button and the position will be saved. On the map you will see all dangerous area.")
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }





}
