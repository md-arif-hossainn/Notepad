package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class forgotpassword extends AppCompatActivity {
    public EditText mforgotpassword;
    public Button mpasswordrecoverbutton;
    public TextView mgobacktologin;

    public FirebaseAuth firebaseAuth;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        getSupportActionBar().hide();

        mforgotpassword = findViewById(R.id.forgotpassword);
        mpasswordrecoverbutton = findViewById(R.id.passwordrecoverbutton);
        mgobacktologin = findViewById(R.id.gobacktologin);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbarofforgetpassword);


        mgobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgotpassword.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mpasswordrecoverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mforgotpassword.getText().toString().trim();
                if(mail.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter Your Mail", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    progressBar.setVisibility(View.VISIBLE);
                    //password recover mail
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Mail Sent,You Can Recover Your Password Using Mail", Toast.LENGTH_SHORT).show();

                                finish();
                                startActivity(new Intent(forgotpassword.this,MainActivity.class));
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Email Is Wrong or Account Not Exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


    }
}