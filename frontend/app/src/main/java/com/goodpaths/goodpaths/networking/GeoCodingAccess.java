package com.goodpaths.goodpaths.networking;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoCodingAccess{


    public GeoCodingAccess(Context context){
        Geocoder geo = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> hello = geo.getFromLocationName("Renens VD", 10);
            Toast.makeText(context, "Hey " + hello.get(0).getLongitude(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
