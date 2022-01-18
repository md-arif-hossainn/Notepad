package com.example.notepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class createnote extends AppCompatActivity  {

    public EditText mcreatetitleofnote,mcreatecontentofnote;
    public FloatingActionButton msavenote;

    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    public FirebaseFirestore firebaseFirestore;

    ProgressBar mprogressbarofcreatenote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);

        mcreatecontentofnote = findViewById(R.id.createcontentofnote);
        mcreatetitleofnote = findViewById(R.id.createtitleofnote);
        msavenote = findViewById(R.id.savenote);

        mprogressbarofcreatenote = findViewById(R.id.progressbarofcreatenote);

        Toolbar toolbar = findViewById(R.id.toolbarofcreatnote);
        setSupportActionBar(toolbar);
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        msavenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = mcreatetitleofnote.getText().toString();
                String content = mcreatecontentofnote.getText().toString();

                if (title.isEmpty() || content.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Both Field Are Required", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    mprogressbarofcreatenote.setVisibility(View.VISIBLE);

                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document();

                    Map<String,Object> note = new HashMap<>();
                    note.put("title",title);
                    note.put("content",content);
                    
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {




                            Toast.makeText(getApplicationContext(), "Note Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(createnote.this,notesActivity.class));

                            
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mprogressbarofcreatenote.setVisibility(View.INVISIBLE);

                            Toast.makeText(getApplicationContext(), "Failed To Create Note", Toast.LENGTH_SHORT).show();
                        }
                    });


                }


            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home)
        {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}