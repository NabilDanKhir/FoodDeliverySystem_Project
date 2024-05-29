package com.example.android.g4_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Feedback extends AppCompatActivity {

    Button submitButton;
    RatingBar ratingStar;
    EditText userInput;
    //Add database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_form);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        submitButton = (Button) findViewById(R.id.submit);
        ratingStar = (RatingBar) findViewById(R.id.ratingBar);
        userInput = findViewById(R.id.comment);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ratingResult = (int) ratingStar.getRating();
                if (ratingResult == 0)
                {
                    Toast.makeText(Feedback.this,"Please rate our app!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Feedback.this, "Thank you for rating our app!! " + ratingResult, Toast.LENGTH_SHORT).show();
                    //Send to database
                }
            }
        });
    }
}
