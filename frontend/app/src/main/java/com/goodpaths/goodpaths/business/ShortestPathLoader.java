package com.goodpaths.goodpaths.business;

import android.content.Context;

import com.android.volley.Request;
import com.goodpaths.common.MyLngLat;
import com.goodpaths.common.ShortestPathQuery;
import com.goodpaths.common.ShortestPathResult;
import com.goodpaths.goodpaths.networking.ServerAccess;
import com.google.android.gms.maps.model.LatLng;

public class ShortestPathLoader {
    private static final String URL = ServerAccess.BASE_URL + "/pathRequest";

    private final ServerAccess<ShortestPathQuery, ShortestPathResult> serverAccess;

    public ShortestPathLoader(Context context, ServerAccess.OnResultHandler<ShortestPathResult> resultHandler) {
        this.serverAccess = new ServerAccess<>(context, Request.Method.POST, URL, resultHandler, ShortestPathResult.class);
    }


    public void makeRequest(LatLng start, LatLng end) throws ServerAccess.ServerAccessException {
        ShortestPathQuery query = new ShortestPathQuery(toLngLat(start), toLngLat(end));
        serverAccess.makeRequest(query);
    }

    private static MyLngLat toLngLat(LatLng latLng) {
        return new MyLngLat(latLng.longitude, latLng.latitude);
    }

}
