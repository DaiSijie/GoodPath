package com.goodpaths.goodpaths;


import com.goodpaths.common.MyLngLat;
import com.google.android.gms.maps.model.LatLng;

public class Utils {

    private Utils(){}

    public static LatLng toLatLng(MyLngLat lngLat) {
        return new LatLng(lngLat.getLat(), lngLat.getLng());
    }
}
