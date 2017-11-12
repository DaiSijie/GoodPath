package com.goodpaths.goodpaths.ui.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.goodpaths.common.Report;
import com.goodpaths.goodpaths.business.CustomUrlTileProvider;
import com.goodpaths.goodpaths.business.DangerPointPoster;
import com.goodpaths.goodpaths.R;
import com.goodpaths.goodpaths.business.LocationProvider;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

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

    // endregion

    // region views

    private ImageButton settingsButton;
    private ImageButton helpButton;
    private FloatingActionButton localizeMeButton;
    private FloatingActionButton recenterButton;

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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        checkLocationPermission();

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Services not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            boolean enabled = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            localizeMeButton.setEnabled(enabled);
            recenterButton.setEnabled(enabled);
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        TileProvider tp = new CustomUrlTileProvider(512, 512);
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
        }
    }

    private void findViews(){
        setSupportActionBar((Toolbar) findViewById(R.id.homeToolbar));
        settingsButton = findViewById(R.id.settings);
        helpButton = findViewById(R.id.help);
        localizeMeButton = findViewById(R.id.button);
        recenterButton = findViewById(R.id.recenterButton);
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
                dangerPointPoster.postCurrentPosition(Report.Type.ACCESSIBILITY);
            }
        });

        localizeMeButton.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
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
    }

    // endregion

}
