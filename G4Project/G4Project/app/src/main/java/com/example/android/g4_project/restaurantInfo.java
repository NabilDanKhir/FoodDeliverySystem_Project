package com.example.android.g4_project;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class restaurantInfo extends AppCompatActivity {
    public static final String TAG = "YOUR-TAG-NAME";

    private static final String PLACES_API_BASE = " https://maps.googleapis.com/maps/api/distancematrix";
    private static final String OUT_JSON = "/json?";
    private static final String API_KEY = "AIzaSyDze-a-LGHzqCRUfjnFJr34SeDkZBXqYvU";

    private static String address;
    private String distance;

    // Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
    final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_info);
        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);

        Double resLat = getIntent().getDoubleExtra(DisplayRestaurants.restaurantLat,0);
        Double resLng = getIntent().getDoubleExtra(DisplayRestaurants.restaurantLong,0);
        String restName = getIntent().getStringExtra(DisplayRestaurants.placeName);
        String restAddr = getIntent().getStringExtra(DisplayRestaurants.placeAddress);
        String restID = getIntent().getStringExtra(DisplayRestaurants.placeID);
        Double curLat = getIntent().getDoubleExtra(DisplayRestaurants.currentLat, 0);
        Double curLng = getIntent().getDoubleExtra(DisplayRestaurants.currentLng, 0);


        Button direction = findViewById(R.id.directionButton);
        TextView title = findViewById(R.id.title);
        TextView snippet = findViewById(R.id.snippet);
        TextView distanceInfo = findViewById(R.id.distanceInfo);

        getAddress(curLat, curLng);
        address = address.replaceAll("\\s","%20");
        findDistanceFromCurrentLoc(address, restID);


        title.setText(restName);
        snippet.setText(restAddr);
        distanceInfo.setText("Distance from Current Location: " + distance);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(restID, fields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(800) // Optional.
                    .setMaxHeight(800) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        });

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getLocation = new Intent(restaurantInfo.this, Maps.class);

                getLocation.putExtra("lati", resLat);
                getLocation.putExtra("long", resLng);
                getLocation.putExtra("title", restName);
                getLocation.putExtra("snippet", restAddr);
                startActivity(getLocation);

            }
        });


    }

    private void getAddress(Double curLat, Double curLng) {
        Geocoder geocoder = new Geocoder(restaurantInfo.this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(curLat,curLng,1);

            address = addressList.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findDistanceFromCurrentLoc(String address, String id) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(OUT_JSON);
            sb.append("origins=" + address);
            sb.append("&destinations=place_id:" + id);
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            System.out.println(url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);

        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);

        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray resultArray = jsonObj.getJSONArray("rows");

            for (int i = 0; i < resultArray.length(); i++) {

                JSONObject jsonObject = resultArray.getJSONObject(i);
                JSONObject getDistances = jsonObject.getJSONArray("elements").getJSONObject(0).getJSONObject("distance");

                distance = getDistances.getString("text");
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error processing JSON results", e);
        }
    }


}