package com.goodpaths.goodpaths.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by fma on 12/11/17.
 */

public class HomeActivityImageHelper {

    private static Bitmap pins;

    public static Bitmap getPins(){
        if(pins == null){
            buildPins();
        }
        return pins;
    }

    private static void buildPins() {
        pins = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(pins);
        Paint p = new Paint();
        p.setARGB(255, 4, 38, 91);
        c.drawCircle(20, 20, 20, p);
        p.setARGB(255, 143, 171, 216);
        c.drawCircle(20, 20, 15, p);
    }




}
