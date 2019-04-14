package com.fleetmanagment.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.fleetmanagment.model.WorkloadData;
import com.fleetmanagment.service.http.GoolgeRouteApi;
import com.fleetmanagment.service.http.GoolgeRouteApi.GoolgeRouteApiProtocol;
import com.fleetmanagment.service.http.WorkloadUpdatePositionApi;
import com.fleetmanagment.service.http.WorkloadUpdatePositionApi.WorkloadUpdatePositionApiProtocol;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoolgeRouteApiProtocol, WorkloadUpdatePositionApiProtocol {
    public static final String WORKLOAD = "workload";
    private LocationManager locationManager;
    private WorkloadData workload;
    private GoogleMap map;
    private GoolgeRouteApi goolgeRouteApi;
    private WorkloadUpdatePositionApi workloadUpdatePositionApi;
    private String username;
    private String accessToken;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final SharedPreferences prefs = getSharedPreferences(LoginActivity.LOGIN, MODE_PRIVATE);
        username = prefs.getString(LoginActivity.USERNAME, null);
        accessToken =prefs.getString(LoginActivity.ACCESSTOKEN, null);

        workload = (WorkloadData) getIntent().getSerializableExtra(WORKLOAD);
        workloadUpdatePositionApi = new WorkloadUpdatePositionApi(this);
        goolgeRouteApi = new GoolgeRouteApi(this);

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setuoUi();
    }

    private void setuoUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trip");
    }

    private void addMarker(final LatLng latLng, final String title) {
        map.addMarker(new MarkerOptions().position(latLng).title(title));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
    }

    private void addRoute(final LatLng start, final LatLng end) {
        //polyline = map.addPolyline(new PolylineOptions()
        //                                   .clickable(true).add(
        //                new LatLng(location.getLatitude(), location.getLongitude()),
        //                new LatLng(Double.parseDouble(workload.destinationLandmark.addressLatitude), Double.parseDouble(workload.destinationLandmark.addressLongitude)))
        //                                   .geodesic(true)
        //);
        goolgeRouteApi.start(start, end);
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                                          * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    public void onStopClick(final View view) {
        if( locationManager != null ) {
            locationManager.removeUpdates(this);
        }
    }

    public void onStartClicks(final View view) {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 50, this);

            double lat = Double.parseDouble(workload.sourceLandmark.addressLatitude);
            double lng = Double.parseDouble(workload.sourceLandmark.addressLongitude);
            String format = "geo:0,0?q=" + lat + "," + lng + "( Location title)";
            Uri uri = Uri.parse(format);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void routesReady(final List<List<HashMap<String, String>>> routes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;
                // Traversing through all the routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                }
                if(lineOptions != null) {
                    map.addPolyline(lineOptions);
                }
            }
        });
    }

    @Override
    public void apiStarted() {

    }

    @Override
    public void apiFinished() {

    }

    @Override
    public void apiFailed(final String error) {

    }

    @Override
    public void onLocationChanged(final Location location) {
        workloadUpdatePositionApi.start(workload.id, location.getAltitude(), location.getLongitude(), username);
        //workloadUpdatePositionApi.start(workload.id, 8.5, 0.04, username);
        if( workload.sourceLandmark != null ) {
            final LatLng start = new LatLng(Double.parseDouble(workload.sourceLandmark.addressLatitude), Double.parseDouble(workload.sourceLandmark.addressLongitude));
            final LatLng end = new LatLng(location.getLatitude(), location.getLongitude());
            //addRoute(start, end);
        }
    }

    @Override
    public void onStatusChanged(final String s, final int i, final Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(final String s) {

    }

    @Override
    public void onProviderDisabled(final String s) {
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;

        if( workload.destinationLandmark != null && workload.sourceLandmark != null ) {
            final LatLng start = new LatLng(Double.parseDouble(workload.sourceLandmark.addressLatitude), Double.parseDouble(workload.sourceLandmark.addressLongitude));
            final LatLng end = new LatLng(Double.parseDouble(workload.destinationLandmark.addressLatitude), Double.parseDouble(workload.destinationLandmark.addressLongitude));
            addRoute(start, end);
            addMarker(start, "Source");
            addMarker(end, "Destination");
        }
    }
}
