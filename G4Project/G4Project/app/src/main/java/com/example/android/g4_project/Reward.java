package com.example.android.g4_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Reward extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_reward);
    }
}