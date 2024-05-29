package com.example.android.g4_project;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity{

    EditText enter_username, enter_password, enter_day, enter_month, enter_year, enter_email, enter_name;
    Button signUp;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        enter_username = findViewById(R.id.enter_username);
        enter_password = findViewById(R.id.enter_password);
        enter_day =  findViewById(R.id.enter_day);
        enter_month = findViewById(R.id.enter_month) ;
        enter_year =  findViewById(R.id.enter_year) ;
        enter_email =  findViewById(R.id.enter_email);
        enter_name =  findViewById(R.id.enter_name);
        progressBar = findViewById(R.id.progressBar);
        signUp = findViewById(R.id.signUp);

        enter_day.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

                if(enter_day.getText().toString().length() == 2)
                {
                    enter_month.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        enter_month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(enter_month.getText().toString().length() == 2)
                {
                    enter_year.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }




    public void registerAcc(View view)
    {
        String username = enter_username.getText().toString().trim();
        String password = enter_password.getText().toString().trim();
        String day = enter_day.getText().toString().trim();
        String month = enter_month.getText().toString().trim();
        String year = enter_year.getText().toString().trim();
        String email = enter_email.getText().toString().trim();
        String displayName = enter_name.getText().toString().trim();

        if(username.isEmpty()){
            enter_username.setError("Username is required");
            enter_username.requestFocus();
            return;
        }

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

        if (displayName.isEmpty()){
            enter_name.setError("Please Enter Display Name");
            enter_name.requestFocus();
            return;
        }

        if(day.isEmpty())
        {
            enter_day.setError("Date of Birth is required");
            enter_day.requestFocus();
            return;
        }

        if(month.isEmpty())
        {
            enter_month.setError("Date of Birth is required");
            enter_month.requestFocus();
            return;
        }

        if(year.isEmpty())
        {
            enter_year.setError("Date of Birth is required");
            enter_year.requestFocus();
            return;
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


        String date = day + "/" + month + "/" + year;


        /***Check first if the username already exists in the database - the documents of "Username" collection ***/
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference checkUN = db.collection("Username").document(username);

        checkUN.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    /***If run successfully and the username exists - halt the process***/
                    DocumentSnapshot document = task.getResult();
                    if(document.exists())
                    {
                        enter_username.setError("Username has been registered before");
                        enter_username.requestFocus();
                        return;
                    }

                    /***If the username doesn't exists and can be registered - continue the process***/
                    else
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(task.isSuccessful())
                                {
                                    /***Keep a copy ot the registered email under the "Username" collection***/
                                    Map<String,Object> copyEmail = new HashMap<>();

                                    copyEmail.put("email", enter_email.getText().toString());
                                    FirebaseFirestore.getInstance().collection("Username").document(username).set(copyEmail);

                                    /***Create the account and store all the information under the "Account" collection ***/
                                    Account account = new Account(username,hash(password),date,email,displayName);

                                    FirebaseFirestore.getInstance().collection("Account").document(email).set(account).addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                //FirebaseFirestore.getInstance().collection("Username").document(username);
                                                FirebaseUser user = mAuth.getCurrentUser();

                                                /***Send a verification email to user once registered and all the fields are stored successfully in "Account" collection ***/
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                                                {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(SignUp.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.INVISIBLE);

                                                            Intent intent = new Intent(getApplicationContext(), Login.class);
                                                            startActivity(intent);
                                                        }

                                                        else
                                                        {
                                                            Toast.makeText(SignUp.this, "Verification Email Not Sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }

                                            else
                                            {
                                                Toast.makeText(SignUp.this, "Failed to Register New Account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                else
                                {
                                    Toast.makeText(SignUp.this, "Error:"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }

                /***If failed to run the check***/
                else
                {
                    Toast.makeText(SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });



    }



    public void login(View view)
    {
        Intent intent = new Intent (this, Login.class);
        startActivity(intent);
    }

    public String hash(String password)
    {
        String salt=getResources().getString(R.string.salt);
        password=password+salt;
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
