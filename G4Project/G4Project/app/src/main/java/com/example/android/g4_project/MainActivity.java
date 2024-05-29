package com.example.android.g4_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    Button rouletteButton;

    public static final String TAG = "YOUR-TAG-NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationViewListener();

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // button to roulette screen
        rouletteButton = (Button) findViewById(R.id.button_roulette);
        rouletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRoulette();
            }
        });

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }//end onCreate

    //nav_drawer
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }//end onOptionItemSelected (nav_drawer)

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_profile:{
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_favourites:{
                Intent intent = new Intent(this, Favourites.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_rewards:{
                Intent intent = new Intent(this, Reward.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_menu:{
                Intent intent = new Intent(this, DisplayRestaurants.class);
                startActivity(intent);
                break;
            }
            /*case R.id.nav_history:{
                Intent intent = new Intent(this, History.class);
                startActivity(intent);
                break;
            }*/
            case R.id.nav_food_roulette:{
                Intent intent = new Intent(this, FoodRoulette.class);
                startActivity(intent);
                break;
            }
            /*case R.id.nav_voucher:{
                Intent intent = new Intent(this, Voucher.class);
                startActivity(intent);
                break;
            }*/
            case R.id.nav_settings:{
                Intent intent = new Intent(this, Notifications.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_feedback:{
                Intent intent = new Intent(this, Feedback.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_help:{
                Intent intent = new Intent(this, Chat1.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_logout:{
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void openRoulette()
    {
        Intent intent = new Intent(this, FoodRoulette.class);
        startActivity(intent);
    }
}