package com.example.notepad;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class notesActivity extends AppCompatActivity  {

    public FloatingActionButton mcreatenotefab;
    public FirebaseAuth firebaseAuth;

    public RecyclerView mrecyclerview;

    public StaggeredGridLayoutManager staggeredGridLayoutManager;

    public FirebaseUser firebaseUser;
    public FirebaseFirestore firebaseFirestore;

    public static FirestoreRecyclerAdapter<Firebasemodel,NoteViewHolder> noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mcreatenotefab = findViewById(R.id.createnotefab);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        getSupportActionBar().setTitle("All Notes");


        mcreatenotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(notesActivity.this,createnote.class));


            }
        });




        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Firebasemodel> allusernote = new FirestoreRecyclerOptions.Builder<Firebasemodel>().setQuery(query,Firebasemodel.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<Firebasemodel,NoteViewHolder>(allusernote) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Firebasemodel model) {


                ImageView popupbutton = holder.itemView.findViewById(R.id.menupopbutton);

                int colourcode = getRandomColor();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colourcode,null));


               holder.notetitle.setText(model.getTitle());
               holder.notecontent.setText(model.getContent());
               String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();

               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       //we have to open detail activity

                       Intent intent = new Intent(view.getContext(),notedetails.class);


                       intent.putExtra("title",model.getTitle());
                       intent.putExtra("content",model.getContent());
                       intent.putExtra("noteId",docId);


                       view.getContext().startActivity(intent);

                   }
               });

               popupbutton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                       popupMenu.setGravity(Gravity.END);
                       popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem menuItem) {

                               Intent intent = new Intent(view.getContext(),editnoteactivity.class);

                               intent.putExtra("title",model.getTitle());
                               intent.putExtra("content",model.getContent());
                               intent.putExtra("noteId",docId);

                               view.getContext().startActivity(intent);

                               return false;
                           }
                       });

                       popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                           @Override
                           public boolean onMenuItemClick(MenuItem menuItem) {


                              // Toast.makeText(getApplicationContext(), "delete successfully", Toast.LENGTH_SHORT).show();
                               DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                               documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void unused) {

                                       Toast.makeText(getApplicationContext(), "This Note Delete Successfully", Toast.LENGTH_SHORT).show();
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {

                                       Toast.makeText(getApplicationContext(), "Failed To Delete", Toast.LENGTH_SHORT).show();
                                   }

                               });
                               return false;
                           }
                       });

                       popupMenu.show();

                   }

               });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recylerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);

      // noteAdapter.startListening();

        mrecyclerview.setItemAnimator(null);


        mrecyclerview.setAdapter(noteAdapter);

    }


    public class NoteViewHolder extends RecyclerView.ViewHolder

    {
        private TextView notetitle;
        private TextView notecontent;
        LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecontent);
            mnote = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(notesActivity.this,MainActivity.class));


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor()
    {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.green);
        colorcode.add(R.color.color1);
        //colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        //colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.color6);
        colorcode.add(R.color.color7);
        colorcode.add(R.color.color8);
        colorcode.add(R.color.color9);
        colorcode.add(R.color.color10);
        colorcode.add(R.color.color11);
        colorcode.add(R.color.purple_200);

        Random random = new Random();
        int number = random.nextInt(colorcode.size());
        return colorcode.get(number);
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}