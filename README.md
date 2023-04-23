# LowCostTube
LowCostTube is an Android app that provides affordable travel options for navigating London using public transportation. The app prioritizes cost-effective journeys, utilizing TfL's services and the osmdroid map library

This project is currently in development so at the moment what it does is:
* displaying all the bus stops for the 24 bus line on an OSMDroid map.
 - this is just to have a taste of Retrofit since I've never used it before

## Dependencies

- Retrofit
- OSMDroid

## Usage

To use this app, simply run it on an Android device or emulator. The app will retrieve the bus stop data from the TFL API using Retrofit and display it on the map using OSMDroid. Again this is not the final use. It's just to test Retrofit and display TFL bus stops on OSMDroid map.

## Code Explanation

- Retrofit is used to retrieve the bus stop data from the TFL API.
- The `StopPoint` class is used to map the JSON data to Java objects.
- The `TflApi` interface defines the endpoints used to retrieve the data.
- The `MainActivity` class uses Retrofit to retrieve the data and display it on the map using OSMDroid.

## Example Code

```java
// Retrofit code to retrieve bus stop data
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

        // Display stop points on the map
        for (StopPoint stopPoint : stopPoints) {
            GeoPoint point = new GeoPoint(stopPoint.getLatitude(), stopPoint.getLongitude());
            Marker marker = new Marker(map);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(stopPoint.getCommonName());
            map.getOverlays().add(marker);
        }
    }

    @Override
    public void onFailure(Call<List<StopPoint>> call, Throwable t) {
        Log.d("MainActivity", "onFailure: " + t.getMessage());
    }
});

```

