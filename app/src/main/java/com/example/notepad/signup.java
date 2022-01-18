package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class signup extends AppCompatActivity  {

    public EditText msignupemail,msignuppassword;
    public RelativeLayout msignup;
    public TextView mgotologin;


    public FirebaseAuth firebaseAuth;

    ProgressBar mprogressbarofsignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        msignupemail = findViewById(R.id.signupemail);
        msignuppassword = findViewById(R.id.signuppassword);
        msignup = findViewById(R.id.signup);
        mgotologin = findViewById(R.id.gotologin);
        mprogressbarofsignup = findViewById(R.id.progressbarofsignup);


        firebaseAuth = FirebaseAuth.getInstance();

        mgotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this,MainActivity.class);
                startActivity(intent);
            }
        });

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = msignupemail.getText().toString().trim();
                String password = msignuppassword.getText().toString().trim();

                if(mail.isEmpty() || password.isEmpty() )

                {
                    Toast.makeText(getApplicationContext(), "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<7)
                {
                    Toast.makeText(getApplicationContext(), "Password Should Greater Than 7 Digits", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //register user to firebase

                      mprogressbarofsignup.setVisibility(View.VISIBLE);

                    firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful())
                            {
                                for (int i=0; i < 2; i++)
                                {
                                    Toast.makeText(getApplicationContext(), "Registration Is Successful Now Verification Your Email", Toast.LENGTH_SHORT).show();
                                }

                                //Toast.makeText(getApplicationContext(), "Registration Is Successful Now Verification Your Email", Toast.LENGTH_SHORT).show();

                                sentEmailVerification();

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Failed To Register", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });


    }
//email verification
    private void sentEmailVerification() {

        FirebaseUser firebaseUser  = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)

        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(getApplicationContext(), "Verification Email Is Already Sent Now Verify & Login Again", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(signup.this,MainActivity.class));


                }
            });
        }
        else
        {

            Toast.makeText(getApplicationContext(), "Failed To Sent Verification Email", Toast.LENGTH_SHORT).show();
        }
    }
}