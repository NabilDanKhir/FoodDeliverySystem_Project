package com.example.android.g4_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;

public class DisplayRestaurants extends AppCompatActivity {

    private ListView mListView;
    private static final String API_KEY = "AIzaSyDze-a-LGHzqCRUfjnFJr34SeDkZBXqYvU";

    public static final String restaurantLat = "Restaurant Latitude";
    public static final String restaurantLong = "Restaurant Longitude";
    public static final String placeName = "Restaurant Name";
    public static final String placeAddress = "Restaurant Address";
    public static final String placeID = "Restaurant PlaceID";
    public static final String currentLat = "Current Latitude";
    public static final String currentLng = "Current Longitude";

    private static ArrayList<Double> latList, lngList;
    private static ArrayList<String> restName;
    private static ArrayList<String> restAddress;
    private static ArrayList<String> restPlaceID;

    private Double devLat;
    private Double devLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        Places.initialize(getApplicationContext(), API_KEY);

        getRestData mm = new getRestData();

        restName = mm.getRestName();
        restAddress = mm.getRestAddress();
        restPlaceID = mm.getRestPlaceID();

        latList = mm.getLatList();
        lngList = mm.getLngList();

        devLat = mm.getDevLat();
        devLng = mm.getDevLng();

        ArrayList<String> list = restName;

        if (list != null) {

            String[] values = list.toArray(new String[list.size()]);
            mListView = findViewById(R.id.listView);
            PlaceImageListView adapter = new PlaceImageListView(this, values);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = mListView.getPositionForView(view);
                    if (position == pos) {
                        Intent restInfo = new Intent(DisplayRestaurants.this, restaurantInfo.class);

                        Double lat = latList.get(pos);
                        Double lng = lngList.get(pos);

                        String restAddr = restAddress.get(pos);
                        String restTitle = restName.get(pos);
                        String restID = restPlaceID.get(pos);

                        restInfo.putExtra(restaurantLat, lat);
                        restInfo.putExtra(restaurantLong, lng);
                        restInfo.putExtra(placeAddress, restAddr);
                        restInfo.putExtra(placeName, restTitle);
                        restInfo.putExtra(placeID, restID);
                        restInfo.putExtra(currentLat, devLat);
                        restInfo.putExtra(currentLng, devLng);

                        startActivity(restInfo);
                    }

                }
            });
        }
    }

}
