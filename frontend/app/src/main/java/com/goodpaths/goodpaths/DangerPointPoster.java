package com.goodpaths.goodpaths;


import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class DangerPointPoster {
    private static final String URL = "http://10.0.2.2:8080/addDanger";

    private static final String PROBLEM_TYPE_KEY = "problemType";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";

    private final Context context;
    private final RequestQueue requestQueue;
    private final LocationProvider locationProvider;

    public DangerPointPoster(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.locationProvider = new LocationProvider(context);
        locationProvider.startLocationUpdates();
    }


    public void postCurrentPosition(ProblemType problemType) {
        LatLng latLng = locationProvider.getLocation();
        postPosition(latLng.latitude, latLng.longitude, problemType);
    }

    private void postPosition(double latitude, double longitude, ProblemType problemType) {
        Request request = createPostRequest(latitude, longitude, problemType);
        requestQueue.add(request);
    }

    private Request createPostRequest(double latitude, double longitude, ProblemType problemType) {

        CustomRequest request = new CustomRequest(
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Danger posted successfully.", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show();
            }
        });
        request.addParam(LATITUDE_KEY, Double.toString(latitude));
        request.addParam(LONGITUDE_KEY, Double.toString(longitude));
        request.addParam(PROBLEM_TYPE_KEY, problemType.name());
        return request;
    }


    private static class CustomRequest extends StringRequest {
        private Map<String, String> params;

        public CustomRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, URL, listener, errorListener);
            params = new HashMap<>();
        }

        public void addParam(String key, String value) {
            params.put(key, value);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }

}
