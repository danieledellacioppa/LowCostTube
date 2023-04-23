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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.tfl.gov.uk/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TflApi api = retrofit.create(TflApi.class);

        Call<List<StopPoint>> call = api.getStopPoints();

        call.enqueue(new Callback<List<StopPoint>>() {
            @Override
            public void onResponse(Call<List<StopPoint>> call, Response<List<StopPoint>> response) {
                if (!response.isSuccessful()) {
                    Log.d("MainActivity", "onResponse: " + response.code());
                    return;
                }

                List<StopPoint> stopPoints = response.body();

                // visualizza la lista di stopPoints nella TextView
                    Log.d("stop-points", stopPoints.toString());

                for (StopPoint stopPoint : stopPoints) {
                    Log.d("stop-point-name", stopPoint.getCommonName()+", LAT= " + stopPoint.getLatitude() + ", LON= " + stopPoint.getLongitude());
                }
            }

            @Override
            public void onFailure(Call<List<StopPoint>> call, Throwable t) {
                Log.d("MainActivity", "onFailure: " + t.getMessage());
            }
        });

    }

    public class StopPoint {
        @SerializedName("id")
        private String id;

        @SerializedName("commonName")
        private String commonName;

        @SerializedName("lat")
        private double latitude;

        @SerializedName("lon")
        private double longitude;

        public String getId() {
            return id;
        }

        public String getCommonName() {
            return commonName;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }


    public interface TflApi {
        @GET("line/24/stoppoints")
        Call<List<StopPoint>> getStopPoints();
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

    public class TfLJourneyRequest {
        public LatLng from;
        public LatLng to;
        public LocalDate date;
        public LocalTime time;
        public String mode = "public";
        public Boolean nationalSearch = true;
        public Boolean alternativeWalking = true;
        public String walkingSpeed = "average";
    }

//    public interface TFLService {
//        // Other API calls...
//
//        @GET("Journey/JourneyResults/{from}/to/{to}")
//        Call<JourneySearchResponse> getJourney(@Path("from") String from, @Path("to") String to, @Query("nationalSearch") boolean nationalSearch, @Query("timeIs") String timeIs, @Query("journeyPreference") String journeyPreference, @Query("app_id") String appId, @Query("app_key") String appKey);
//    }

//    public class JourneySearchResponse {
//        @SerializedName("$type")
//        public String type;
//
//        public List<Journey> journeys;
//    }
//
//    public class Journey {
//        public List<Leg> legs;
//    }

//    public class Leg {
//        public String modeName;
//        public String destinationName;
//        public String instruction;
//        public String departureTime;
//        public String arrivalTime;
//        public String duration;
//        public List<StopPoint> path;
//    }

//    public class StopPoint {
//        public double lat;
//        public double lon;
//    }

//    public class TFLApiClient {
//        private static final String BASE_URL = "https://api.tfl.gov.uk/";
//
//        private TFLService service;
//
//        public TFLService getClient() {
//            if (service == null) {
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl(BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .build();
//
//                service = retrofit.create(TFLService.class);
//            }
//            return service;
//        }
//    }

//    public interface TflApiService {
//        @GET("Journey/JourneyResults/{from}/to/{to}")
//        Call<JourneyPlannerResponse> getJourneyPlannerResponse(@Path("from") String from, @Path("to") String to, @Query("nationalSearch") boolean nationalSearch, @Query("date") String date, @Query("time") String time, @Query("timeIs") String timeIs, @Query("journeyPreference") String journeyPreference, @Query("mode") String mode, @Query("accessibilityPreference") String accessibilityPreference, @Query("fromIsWalking") boolean fromIsWalking, @Query("toIsWalking") boolean toIsWalking, @Query("walkingSpeed") String walkingSpeed, @Query("cyclePreference") String cyclePreference, @Query("adjustment") String adjustment, @Query("bikeProficiency") String bikeProficiency, @Query("alternativeCycle") boolean alternativeCycle, @Query("alternativeWalking") boolean alternativeWalking, @Query("applyHtmlMarkup") boolean applyHtmlMarkup, @Query("useMultiModalCall") boolean useMultiModalCall, @Query("walkingOptimization") String walkingOptimization, @Query("taxiOnlyTrip") boolean taxiOnlyTrip, @Query("walkingTime") int walkingTime);
//    }

//    public class JourneyPlannerResponse {
//        @SerializedName("$type")
//        @Expose
//        private String type;
//        @SerializedName("journeys")
//        @Expose
//        private List<Journey> journeys = null;
//        // getter and setter methods here
//    }

    public class Instruction {
        @SerializedName("$type")
        @Expose
        private String type;
        @SerializedName("summary")
        @Expose
        private String summary;
        // getter and setter methods here
    }

    public class ArrivalPoint {
        @SerializedName("$type")
        @Expose
        private String type;
        @SerializedName("commonName")
        @Expose
        private String commonName;
        // getter and setter methods here
    }

    public class DeparturePoint {
        @SerializedName("$type")
        @Expose
        private String type;
        @SerializedName("commonName")
        @Expose
        private String commonName;
        // getter and setter methods here
    }

    public class Arrival {
        @SerializedName("id")
        private String id;
        @SerializedName("lineName")
        private String lineName;
        @SerializedName("platformName")
        private String platformName;
        @SerializedName("towards")
        private String towards;
        @SerializedName("expectedArrival")
        private String expectedArrival;
        // add getters and setters for each field
    }

//    public List<Arrival> getArrivals(String stopId) throws IOException {
//        TFLApi tflApi = retrofit.create(TFLApi.class);
//        Call<List<Arrival>> call = tflApi.getArrivals(stopId, TFL_APP_ID, TFL_APP_KEY);
//        Response<List<Arrival>> response = call.execute();
//        if (response.isSuccessful()) {
//            return response.body();
//        } else {
//            throw new IOException("Failed to get arrivals: " + response.errorBody().string());
//        }
//    }



}