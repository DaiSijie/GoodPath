package com.goodpaths.goodpaths.networking;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoCodingAccess{

    public static void getLongLat(Context context, final String name, final OnCoolResultListener listener){
        final Geocoder geo = new Geocoder(context, Locale.getDefault());

        AsyncTask<Void, Void, LatLng> hep = new AsyncTask<Void, Void, LatLng>(){

            @Override
            protected LatLng doInBackground(Void... voids) {
                try {
                    Address a = geo.getFromLocationName(name, 10).get(0);
                    LatLng toReturn = new LatLng(a.getLatitude(), a.getLongitude());
                    return toReturn;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(LatLng latLng) {
                super.onPostExecute(latLng);
                listener.onCoolResultReceived(latLng);
            }
        };

        hep.execute();
    }




    public interface OnCoolResultListener{
        public void onCoolResultReceived(LatLng destLocation);
    }


}
