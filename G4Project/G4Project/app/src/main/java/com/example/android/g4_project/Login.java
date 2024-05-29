package com.example.android.g4_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText enter_email, enter_password;
    Button resend;
    FirebaseAuth mAuth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        enter_email = findViewById(R.id.enter_email);
        enter_password = findViewById(R.id.enter_password);
        resend = findViewById(R.id.resend);

    }

    public void login(View view)
    {
        String email = enter_email.getText().toString();
        String password = enter_password.getText().toString();

        if (email.isEmpty()) {
            enter_email.setError("Please Enter Your Email");
            enter_email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enter_email.setError("Please Enter A Valid Email");
            enter_email.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            enter_password.setError("Please Enter Password");
            enter_password.requestFocus();
            return;
        }

        if (password.length() < 10) {
            enter_password.setError("Password Should Be At Least 10 Characters");
            enter_password.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();


        ProgressBar progressBar = findViewById(R.id.progressBar);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                if (task.isSuccessful())
                {
                    if(user != null)
                    {
                        progressBar.setVisibility(View.VISIBLE);

                        /***If the email is verified***/
                        if (user.isEmailVerified())
                        {
                            Intent intent = new Intent(getApplicationContext(), getRestData.class);
                            startActivity(intent);
                        }
                        /***If the email is not yet verified***/
                        else if (!user.isEmailVerified())
                        {
                            Toast.makeText(Login.this, "Email is not yet verified", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.INVISIBLE);
                            resend.setVisibility(View.VISIBLE); /***Enable the "Resend Verification Email" button ***/

                            resend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(Login.this, "Verificatioon Email Resent", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Login.this, "Verification Email Failed to Resend" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                }
                            });

                        }
                    }

                    else{    /***To prevent force close when the app installed for the first time***/
                        Toast.makeText(Login.this, "Null", Toast.LENGTH_SHORT).show();
                    }
                }


                else
                {
                    Toast.makeText(Login.this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void signUp(View view)
    {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    public void resetPassword(View view)
    {

        EditText resetPW = new EditText(view.getContext());
        AlertDialog.Builder resetPwDialog = new AlertDialog.Builder(this);
        resetPwDialog.setTitle("Reset Password?");
        resetPwDialog.setMessage("Kindly Enter Your Registered Email:");
        resetPwDialog.setView(resetPW);


        resetPwDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = resetPW.getText().toString().trim();


                if(email.isEmpty()){
                    Toast.makeText(Login.this, "Email Cannot Be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(Login.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Password-Reset Email Has Been Sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        resetPwDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        resetPwDialog.show().create();

    }
}


