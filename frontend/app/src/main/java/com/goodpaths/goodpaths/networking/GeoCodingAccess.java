package com.goodpaths.goodpaths.networking;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.goodpaths.common.MyLngLat;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GeoCodingAccess{

    public static void getLongLat(Context context, final String name, final OnCoolResultListener listener){
        final Geocoder geo = new Geocoder(context, Locale.getDefault());

        AsyncTask<Void, Void, LatLng> hep = new AsyncTask<Void, Void, LatLng>(){

            @Override
            protected LatLng doInBackground(Void... voids) {
                try {
                    List<Address> addresses = geo.getFromLocationName(name, 10);

                    //only keep what is in the Area...

                    Iterator<Address> it = addresses.iterator();
                    while(it.hasNext()){
                        Address current = it.next();
                        MyLngLat test = new MyLngLat(current.getLongitude(), current.getLatitude());
                        if(!inLausanne(test)){
                            it.remove();
                        }
                    }

                    if(addresses.isEmpty()){
                        return null;
                    }

                    // return any.
                    Address address = geo.getFromLocationName(name, 1).get(0);
                    LatLng toReturn = new LatLng(address.getLatitude(), address.getLongitude());
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

    private static boolean inLausanne(MyLngLat destination){
        // best hardcode you'll ever see
        boolean lngOk = destination.lng < 6.748695373535156 && destination.lng > 6.443138122558594;
        boolean latOk = destination.lat < 46.59369331115965 && destination.lat > 46.48789797624174;
        return lngOk && latOk;
    }

    public interface OnCoolResultListener{

        void onCoolResultReceived(LatLng destLocation);

    }

}
