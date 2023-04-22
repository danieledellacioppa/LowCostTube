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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context context ;
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

        context = this;

        setContentView(R.layout.activity_main);
//        Toast.makeText(context, "onCreate", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onCreate");

        MapView mapView;
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
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

    public class MapFragment extends Fragment implements MapListener {

        private MapView mapView;


        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
        {
            Toast.makeText(context, "MapFragment", Toast.LENGTH_SHORT).show();
            requestPermissionsIfNecessary(new String[] {
                    //current location
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    // WRITE_EXTERNAL_STORAGE is required in order to show the map
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });

            View view = inflater.inflate(R.layout.activity_main, container, false);
            mapView = findViewById(R.id.mapView);
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setMultiTouchControls(true);
            return view;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.d("MapFragment", "onCreate");
            Toast.makeText(context, "onCreate", Toast.LENGTH_SHORT).show();
            Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

            IMapController mapController = mapView.getController();
            mapController.setZoom(12.0);
            GeoPoint startPoint = new GeoPoint(51.5074, -0.1278);
            mapController.setCenter(startPoint);

            // ...
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
            mapView.setMultiTouchControls(true);
            mapView.getController().setZoom(15.0);
            mapView.getController().setCenter(new GeoPoint(51.5074, -0.1278));
        }

        @Override
        public boolean onScroll(ScrollEvent event) {
            // Esempio di operazione eseguita quando si fa lo scroll sulla mappa
            return false;
        }

        @Override
        public boolean onZoom(ZoomEvent event) {
            // Esempio di operazione eseguita quando si fa lo zoom sulla mappa
            return false;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        public void onResume() {
            super.onResume();
            mapView.onResume();
        }

        public void onPause() {
            super.onPause();
            mapView.onPause();
        }

        public void onDestroy() {
            super.onDestroy();
//            mapView.onDestroy();
        }
    }

}