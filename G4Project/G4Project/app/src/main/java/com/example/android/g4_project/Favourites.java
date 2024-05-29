package com.example.android.g4_project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Favourites extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
