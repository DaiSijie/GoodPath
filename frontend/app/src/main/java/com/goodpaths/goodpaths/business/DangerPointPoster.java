package com.goodpaths.goodpaths.business;


import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.goodpaths.common.Report;
import com.goodpaths.goodpaths.networking.ServerAccess;
import com.google.android.gms.maps.model.LatLng;

public class DangerPointPoster {
    private static final String URL = ServerAccess.BASE_URL + "/addDanger";

    private final Context context;
    private final LocationProvider locationProvider;

    private final ServerAccess<Report, Report> serverAccess;

    public DangerPointPoster(Context context) {
        this.context = context;
        this.locationProvider = new LocationProvider(context);
        locationProvider.startLocationUpdates();
        serverAccess = createServerAccess();
    }

    private ServerAccess<Report, Report> createServerAccess() {
        return new ServerAccess<>(
                context,
                Request.Method.POST,
                URL,
                new PostReportResultHandler(),
                Report.class);
    }

    public void postCurrentPosition(Report.Type problemType) {
        LatLng latLng = locationProvider.getLocation();
        postPosition(new Report(problemType, latLng.latitude, latLng.longitude));
    }

    private void postPosition(Report report) {
        try {
            serverAccess.makeRequest(report);
        } catch (ServerAccess.ServerAccessException e) {
            Toast.makeText(context, "Failed to report danger.", Toast.LENGTH_LONG).show();
        }
    }

    private class PostReportResultHandler implements ServerAccess.OnResultHandler<Report> {
        @Override
        public void onSuccess(Report response) {
            Toast.makeText(context, "Danger successfully reported.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError() {
            Toast.makeText(context, "Failed to report danger.", Toast.LENGTH_LONG).show();
        }
    }


}
