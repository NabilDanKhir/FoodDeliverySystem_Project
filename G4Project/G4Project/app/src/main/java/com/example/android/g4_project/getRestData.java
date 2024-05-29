package com.example.android.g4_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class getRestData extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private View mLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    public static final String currentLat = "Current Latitude";
    public static final String currentLng = "Current Longitude";
    private static final String KEY_LOCATION = "location";

    private static final String PLACES_API_BASE = " https://maps.googleapis.com/maps/api/place";
    private static final String API_KEY = "AIzaSyDze-a-LGHzqCRUfjnFJr34SeDkZBXqYvU";
    private static final String TYPE_DISTANCE = "/details";
    private static final String TYPE_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json?";
    private static final String LOG_TAG = "ListRest";

    public String nextPtoken;


    private static ArrayList<Double> latList, lngList;
    private static ArrayList<String> restName;
    private static ArrayList<String> restAddress;
    private static ArrayList<String> restPlaceID;

    private static Double lat = 3.0759497261748168;
    private static Double lng = 101.5890387358919;

    private static Double devLat;
    private static Double devLng;
    private static int radius = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);
        mLayout = findViewById(R.id.drawer_layout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        showRestaurants();

    }//end onCreate

    private void startRestaurants() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent startResIntent = new Intent(this, MainActivity.class);
            startActivity(startResIntent);

        }
    }

    private void showRestaurants() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        devLat = lastKnownLocation.getLatitude();
                        devLng = lastKnownLocation.getLongitude();
                        searchRestaurant(lat,lng,radius);
                        startRestaurants();
                    }
                }
            });

        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(mLayout, "Location permission is required to display restaurants",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(getRestData.this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, PERMISSION_REQUEST_LOCATION);
                }
            }).show();
        } else {
            Snackbar.make(mLayout, "Permission is not available. Requesting again", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_LOCATION);
        }
    }


    public void searchRestaurant(Double lat, Double lng, int radius) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
            sb.append("&type=restaurant");
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);

        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Thread.sleep(2000);
            nextPtoken = jsonObj.getString("next_page_token");
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the descriptions from the results

            latList = new ArrayList<Double>(60);
            lngList = new ArrayList<Double>(60);
            restName = new ArrayList<String>(60);
            restAddress = new ArrayList<String>(60);
            restPlaceID = new ArrayList<String>(60);

            for (int i = 0; i < predsJsonArray.length(); i++) {

                JSONObject jsonObjLoc = predsJsonArray.getJSONObject(i);
                JSONObject jsonLatLng = jsonObjLoc.getJSONObject("geometry").getJSONObject("location");

                String latitude = jsonLatLng.getString("lat");
                String longitude = jsonLatLng.getString("lng");

                String name = predsJsonArray.getJSONObject(i).getString("name");
                String address = predsJsonArray.getJSONObject(i).getString("vicinity");


                String id = predsJsonArray.getJSONObject(i).getString("place_id");

                restName.add(name);
                restAddress.add(address);
                restPlaceID.add(id);
                latList.add(Double.parseDouble(latitude));
                lngList.add(Double.parseDouble(longitude));

            }


        } catch (JSONException | InterruptedException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        StringBuilder secondResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
            sb.append("&type=restaurant");
            sb.append("&key=" + API_KEY);
            sb.append("&pagetoken=" + nextPtoken);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                secondResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);

        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(secondResults.toString());
            Thread.sleep(4000);
            nextPtoken = jsonObj.getString("next_page_token");
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the descriptions from the results

            for (int i = 0; i < predsJsonArray.length(); i++) {

                JSONObject jsonObjLoc = predsJsonArray.getJSONObject(i);
                JSONObject jsonLatLng = jsonObjLoc.getJSONObject("geometry").getJSONObject("location");

                String latitude = jsonLatLng.getString("lat");
                String longitude = jsonLatLng.getString("lng");

                String name = predsJsonArray.getJSONObject(i).getString("name");
                String address = predsJsonArray.getJSONObject(i).getString("vicinity");


                String id = predsJsonArray.getJSONObject(i).getString("place_id");

                restName.add(name);
                restAddress.add(address);
                restPlaceID.add(id);
                latList.add(Double.parseDouble(latitude));
                lngList.add(Double.parseDouble(longitude));


            }

        } catch (JSONException | InterruptedException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

        StringBuilder thirdResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("location=" + String.valueOf(lat) + "," + String.valueOf(lng));
            sb.append("&radius=" + String.valueOf(radius));
            sb.append("&type=restaurant");
            sb.append("&key=" + API_KEY);
            sb.append("&pagetoken=" + nextPtoken);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                thirdResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);

        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(thirdResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the descriptions from the results

            for (int i = 0; i < predsJsonArray.length(); i++) {

                JSONObject jsonObjLoc = predsJsonArray.getJSONObject(i);
                JSONObject jsonLatLng = jsonObjLoc.getJSONObject("geometry").getJSONObject("location");

                String latitude = jsonLatLng.getString("lat");
                String longitude = jsonLatLng.getString("lng");

                String name = predsJsonArray.getJSONObject(i).getString("name");
                String address = predsJsonArray.getJSONObject(i).getString("vicinity");


                String id = predsJsonArray.getJSONObject(i).getString("place_id");

                restName.add(name);
                restAddress.add(address);
                restPlaceID.add(id);
                latList.add(Double.parseDouble(latitude));
                lngList.add(Double.parseDouble(longitude));

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON results", e);
        }

    }

    public void setCurrentLng (Double lng) {
        this.lng = lng;
    }

    public Double getDevLat() {
        return devLat;
    }

    public void setDevLat (Double devLat) {
        this.devLat = devLat;
    }

    public Double getDevLng() {
        return devLng;
    }

    public void setDevLng (Double devLng) {
        this.devLng = devLng;
    }

    public ArrayList<String> getRestName(){
        return restName;
    }

    public void setRestName (ArrayList<String> restName) {
        this.restName = restName;
    }

    public ArrayList<String> getRestAddress(){
        return restAddress;
    }

    public void setRestAddress(ArrayList<String> restAddress) {
        this.restAddress = restAddress;
    }

    public ArrayList<String> getRestPlaceID(){
        return restPlaceID;
    }

    public void setLatList (ArrayList<Double> latList) {
        this.latList = latList;
    }

    public ArrayList<Double> getLatList(){
        return latList;
    }

    public void setLngList (ArrayList<Double> lngList) {
        this.lngList = lngList;
    }

    public ArrayList<Double> getLngList(){
        return lngList;
    }

}