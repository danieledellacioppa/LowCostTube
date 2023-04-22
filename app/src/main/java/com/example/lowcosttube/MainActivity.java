package com.example.lowcosttube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.caverock.androidsvg.BuildConfig;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context context;
    MapView mapView;
    MyLocationNewOverlay mLocationOverlay;


    private RotationGestureOverlay mRotationGestureOverlay; //variable to allow rotation gestures

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        requestPermissionsIfNecessary(new String[] {
                //current location
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        Context ctx = getApplicationContext();  // here we get the context using
        // getApplicationContext() rather than assigning
        // it to "this"

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // setting this before the layout is inflated is a good idea
        // it 'should' ensure that the map has a writable location for the map cache, even without
        // permissions if no tiles are displayed, you can try overriding the cache path using
        // Configuration.getInstance().setCachePath
        // see also StorageUtils note, the load method also sets the HTTP User Agent to your
        // application's package name, abusing osm's tile servers will get you banned based on
        // this string

        context = this;

        setContentView(R.layout.activity_main);
//        Toast.makeText(context, "onCreate", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onCreate");

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(12.0);
        GeoPoint startPoint = new GeoPoint(51.5074, -0.1278);
        mapController.setCenter(startPoint);

        mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(true);
        mapView.setMultiTouchControls(true);
        mapView.getOverlays().add(this.mRotationGestureOverlay);

        showMyLocation();           //show device location on the map
    }

    public void showMyLocation()
    {
        //Here we just show a little yellow man on any Icon representing the actual device position
        //which gets updated as the device moves around the world. When moving then the
        // icon becomes a grey arrow point the movement's direction
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context),mapView); //we get the device location
        this.mLocationOverlay.enableMyLocation();       //we enable it
        mapView.getOverlays().add(this.mLocationOverlay);   //we overlay it on the map
    }

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private void requestPermissionsIfNecessary(String[] permissions)
    {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED)
            {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0)
        {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

}