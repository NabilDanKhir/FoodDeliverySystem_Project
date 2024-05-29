package com.example.android.g4_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText enter_username, enter_password, enter_name, enter_date, enter_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, Chat1.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null) {

            enter_username = findViewById(R.id.enter_username);
            enter_password = findViewById(R.id.enter_password);
            enter_name = findViewById(R.id.enter_name);
            enter_date = findViewById(R.id.enter_date);

            String email = user.getEmail();
            enter_email = findViewById(R.id.enter_email);
            enter_email.setText(email);


            DocumentReference doc = db.collection("Account").document(email);
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();

                        if(document.exists())
                        {
                            String un = document.getString("userName");
                            String dn = document.getString("displayName");
                            String date = document.getString("date");

                            enter_username.setText(un);
                            enter_name.setText(dn);
                            enter_date.setText(date);
                            //Toast.makeText(Profile.this, "Data: " + document.getData(), Toast.LENGTH_SHORT).show();

                        }

                        else{
                            Toast.makeText(Profile.this, "No document", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else{
                        Toast.makeText(Profile.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void save(View view)
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        enter_name = findViewById(R.id.enter_name);
        enter_date = findViewById(R.id.enter_date);
        enter_email = findViewById(R.id.enter_email);
        enter_password = findViewById(R.id.enter_password);

        String displayName = enter_name.getText().toString().trim();
        String date = enter_date.getText().toString().trim();
        String email = enter_email.getText().toString();
        String password = enter_password.getText().toString().trim();
        String pw = enter_password.getText().toString();
        String oldEmail = email;

        if(password.isEmpty()){
            enter_password.setError("Please Enter Password");
            enter_password.requestFocus();
            return;
        }

        if (password.length() <10){
            enter_password.setError("Password Should Be At Least 10 Characters");
            enter_password.requestFocus();
            return;
        }

        if(date.isEmpty())
        {
            enter_date.setError("Date of Birth is required");
            enter_date.requestFocus();
        }


        if(email.isEmpty()){
            enter_email.setError("Please Enter Your Email");
            enter_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            enter_email.setError("Please Enter A Valid Email");
            enter_email.requestFocus();
            return;
        }

        if (displayName.isEmpty()){
            enter_name.setError("Please Enter Display Name");
            enter_name.requestFocus();
            return;
        }


        user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {

                    user.updatePassword(pw);

                    DocumentReference doc = db.collection("Account").document(user.getEmail());

                    Map<String,Object> update = new HashMap<>();
                    update.put("userName", enter_username.getText().toString());
                    update.put("password", hash(enter_password.getText().toString()));
                    update.put("displayName", enter_name.getText().toString());
                    update.put("date", enter_date.getText().toString());
                    update.put("email", user.getEmail());

                    doc.set(update, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(Profile.this, "Account Information Updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                                /*if(!oldEmail.equals(email))
                                {
                                    db.collection("Account").document(oldEmail).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(Profile.this, "Deleted", Toast.LENGTH_SHORT).show();
                                            }

                                            else
                                            {
                                                Toast.makeText(Profile.this, "Not Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }*/

                                /***To get the username of updated email - as a reference to update the email field under "Username" collection***/
                                DocumentReference doc2 = db.collection("Account").document(user.getEmail());
                                doc2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if ((task.isSuccessful())){

                                            DocumentSnapshot result = task.getResult();

                                            /***Get the username value of updated email and assign to "un" ***/
                                            String un = result.getString("userName");

                                            /***Grab the intended document in "Username" collection ***/
                                            DocumentReference doc3 = db.collection("Username").document(un);

                                            Map<String,Object> upt = new HashMap<>();
                                            upt.put("email", user.getEmail()); /*** Get the latest updated email ***/
                                            doc3.set(upt); /*** Update the email field in "Username collection ***/

                                        }

                                        else{
                                            Toast.makeText(Profile.this, "Failed to Uodate Username Collection", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }

                            else{
                                Toast.makeText(Profile.this, "Failed to Update Account Information " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                else
                {
                    Toast.makeText(Profile.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void cancel(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public String hash(String password)
    {
        String salt=getResources().getString(R.string.salt);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }


}