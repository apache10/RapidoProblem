package com.example.gauravgupta.rapidodemo;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gauravgupta.rapidodemo.Modules.DirectionFinder;
import com.example.gauravgupta.rapidodemo.Modules.DirectionFinderListener;
import com.example.gauravgupta.rapidodemo.Modules.Route;
import com.example.gauravgupta.rapidodemo.constants.ProjectConstant;
import com.example.gauravgupta.rapidodemo.rest.RetrofitClient;
import com.example.gauravgupta.rapidodemo.rest.RetrofitInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private static String TAG = "DEBUGGER";
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    int color = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onPathFind(View view) {
        EditText E1 = (EditText) findViewById(R.id.txt_source);
        EditText E2 = (EditText) findViewById(R.id.txt_destination);
        String source = E1.getText().toString();
        String destination = E2.getText().toString();
        color = 0;
        if (source == null || destination == null || source.equals("") || destination.equals(""))
            return;
        Log.d(TAG, "" + source + destination);
        try {
            new DirectionFinder(this, source, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LatLng bangalore = new LatLng(12.9716, 77.5946);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.addMarker(new MarkerOptions().position(bangalore).title("Bangalore"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((bangalore), 12.0f));
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12.0f));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.man))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.man))
                    .title(route.endAddress)
                    .position(route.endLocation)));
            PolylineOptions polylineOptions;
            if (color % 10 == 0) {
                polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);
                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));

            } else if (color % 10 == 1) {
                polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.YELLOW).
                        width(10);
                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));
            }
            color++;
        }
    }

    public void directionInfoApi(LatLng l,int radius, String type){
        final String s=type;
        RetrofitInterface apiService = RetrofitClient.getClient().create(RetrofitInterface.class);
        Call<JsonObject> call= apiService.getDirectionInfo(""+l.latitude+"," + l.longitude, ProjectConstant.GoogleApiKey);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Response size "," " + response.body().get("results").getAsJsonArray().size());
                Log.d("Response " , response.body().get("results").toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Response", "Error");
            }
        });
    }


}
