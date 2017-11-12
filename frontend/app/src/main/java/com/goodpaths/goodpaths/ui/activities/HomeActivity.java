package com.goodpaths.goodpaths.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.goodpaths.common.MyLngLat;
import com.goodpaths.common.Report;
import com.goodpaths.common.ShortestPathResult;
import com.goodpaths.goodpaths.Utils;
import com.goodpaths.goodpaths.business.CustomUrlTileProvider;
import com.goodpaths.goodpaths.business.DangerPointPoster;
import com.goodpaths.goodpaths.R;
import com.goodpaths.goodpaths.business.DangerTypeHelper;
import com.goodpaths.goodpaths.business.LocationProvider;

import com.goodpaths.goodpaths.business.ShortestPathLoader;
import com.goodpaths.goodpaths.networking.GeoCodingAccess;
import com.goodpaths.goodpaths.networking.ServerAccess;
import com.goodpaths.goodpaths.ui.HomeActivityImageHelper;
import com.goodpaths.goodpaths.ui.InfoOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, LocationProvider.OnNewLocationReceivedListener, DangerPointPoster.OnMegaSuccessListener {

    // region business

    private static int LOCATION_PERMISSION_REQUEST = 1;

    private GoogleMap map;
    private DangerPointPoster dangerPointPoster;
    private TileOverlay tileOverlay;

    // endregion

    // region state

    private boolean alreadyMoved = false;
    private Marker currentPins;
    private Polyline lastAddedPath;
    private Marker lastDestination;

    // endregion

    // region views

    private ImageButton settingsButton;
    private ImageButton helpButton;
    private FloatingActionButton localizeMeButton;
    private FloatingActionButton goFAB;
    private FloatingActionButton recenterButton;
    private SupportMapFragment mapFragment;
    private EditText destinationText;
    private Button goButton;
    private LinearLayout goBaseLayout;
    private ProgressDialog progress;

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        initViews();

        LocationProvider.getInstanceOf(this).setOnNewLocationReceivedListener(this);
        dangerPointPoster = new DangerPointPoster(this);
        dangerPointPoster.setOnMegaSuccessListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationPermission();
        Fragment f = (Fragment) mapFragment;

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Services not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            boolean enabled = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            localizeMeButton.setEnabled(enabled);
            recenterButton.setEnabled(enabled);
            goFAB.setEnabled(enabled);
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        TileProvider tp = new CustomUrlTileProvider(512, 512, this);
        tileOverlay = this.map.addTileOverlay(new TileOverlayOptions().tileProvider(tp).transparency(0.5f).fadeIn(true));
    }

    @Override
    public void onNewLocationReceived(LatLng location) {
        if(currentPins != null){
            currentPins.remove();
        }

        BitmapDescriptor toShow = BitmapDescriptorFactory.fromBitmap(HomeActivityImageHelper.getPins());
        currentPins = map.addMarker(new MarkerOptions().position(location).title("Me").icon(toShow));

        if(!alreadyMoved){
            alreadyMoved = true;
            centerOn(location);
        }
    }

    @Override
    public void onSuccess() {
        refresh();
    }

    // region helpers

    private void refresh(){
        if(tileOverlay != null){
            tileOverlay.clearTileCache();
        }
        if(lastAddedPath != null){
            lastAddedPath.remove();
        }
        if(lastDestination != null){
            lastDestination.remove();
        }
    }

    private void centerOn(LatLng location){
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
        map.moveCamera(CameraUpdateFactory.zoomTo(16f));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            localizeMeButton.setEnabled(true);
            recenterButton.setEnabled(true);
            goFAB.setEnabled(true);
        }
    }

    private void findViews(){
        setSupportActionBar((Toolbar) findViewById(R.id.homeToolbar));
        settingsButton = findViewById(R.id.settings);
        helpButton = findViewById(R.id.help);
        localizeMeButton = findViewById(R.id.button);
        recenterButton = findViewById(R.id.recenterButton);
        destinationText = findViewById(R.id.destinationText);
        goButton = findViewById(R.id.goButton);
        goFAB = findViewById(R.id.goFAB);
        goBaseLayout = findViewById(R.id.goBaseLayout);
    }

    private void initViews(){
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoOverlay.displayOverlay(HomeActivity.this);
            }
        });

        localizeMeButton.setEnabled(false);
        localizeMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Report.Type toUse = DangerTypeHelper.getInstance().getDangerType(null) == DangerTypeHelper.ACCESSIBILITY ? Report.Type.ACCESSIBILITY : Report.Type.HARASSMENT;
                dangerPointPoster.postCurrentPosition(toUse);
            }
        });

        localizeMeButton.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                if(DangerTypeHelper.getInstance().getDangerType(HomeActivity.this) == DangerTypeHelper.ACCESSIBILITY){
                    return false;
                }

                Vibrator v = (Vibrator) getSystemService(HomeActivity.VIBRATOR_SERVICE);
                v.vibrate(500);
                String uri = "tel:123456789";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);

                return true;
            }
        });

        recenterButton.setEnabled(false);
        recenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerOn(LocationProvider.getInstanceOf(HomeActivity.this).getLocation());
            }
        });

        goFAB.setEnabled(false);
        goFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DangerTypeHelper.getInstance().getDangerType(HomeActivity.this) == DangerTypeHelper.ACCESSIBILITY){
                    showError("Not available for accessibility");
                }
                else {
                    int toPut = goBaseLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    goBaseLayout.setVisibility(toPut);
                }
            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String destination = destinationText.getText().toString();
                if(lastAddedPath != null) {
                    lastAddedPath.remove();
                }
                if(lastDestination != null) {
                    lastDestination.remove();
                }
                goBaseLayout.setVisibility(View.GONE);
                destinationText.setText("");

                //hide keyboard
                View focused = HomeActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                //start the long way to downloading a path...
                showLoadingDialog();
                requestLocation(destination);

            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("Looking for the best path...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
    }

    private void requestLocation(String destination){
        GeoCodingAccess.getLongLat(HomeActivity.this, destination, new GeoCodingAccess.OnCoolResultListener() {

            @Override
            public void onCoolResultReceived(LatLng destLocation) {
                if(destLocation == null){
                    showError("Destination unknown");
                    return;
                }

                // if destination outside Lausanne...

                LatLng startLocation = LocationProvider.getInstanceOf(HomeActivity.this).getLocation();

                if(startLocation == null) {
                    showError("Start unknown");
                    return;
                }

                lastDestination = map.addMarker(new MarkerOptions().position(destLocation).title("Destination"));
                requestPath(startLocation, destLocation);
            }
        });
    }

    /*
     * CALLS ...
     */

    private void requestPath(LatLng start, LatLng end){
        ShortestPathLoader loader = new ShortestPathLoader(HomeActivity.this, new ServerAccess.OnResultHandler<ShortestPathResult>() {

            @Override
            public void onSuccess(ShortestPathResult response) {
                displayPath(toLatLng(response.getNodes()));
            }

            private List<LatLng> toLatLng(List<MyLngLat> nodes) {
                List<LatLng> latLngs = new ArrayList<>(nodes.size());
                for(MyLngLat n: nodes) {
                    latLngs.add(Utils.toLatLng(n));
                }
                return latLngs;
            }

            @Override
            public void onError() {
                showError("Best path request failed");
            }
        });

        try {
            loader.makeRequest(start, end);
        } catch (ServerAccess.ServerAccessException e) {
            showError("Couldn't place request for best path");
        }
    }

    /*
     * CALLS ...
     */

    private void displayPath(List<LatLng> path) {
        if(path.isEmpty()){
            showError("Path was empty");
            return;
        }
        hideLoadingDialog();
        lastAddedPath = map.addPolyline(new PolylineOptions().addAll(path));
        lastAddedPath.setColor(0xff8fabd5);
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(46.5343928, 6.60614029)));
        map.animateCamera(CameraUpdateFactory.zoomTo(12f));

    }

    /*
     * FALLBACK :-(
     */

    private void showError(String error){
        hideLoadingDialog();
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void showLoadingDialog(){
        progress.show();
    }

    private void hideLoadingDialog(){
        progress.dismiss();
    }

    // endregion

}
