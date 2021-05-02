package com.BloomingSouls;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

public class UOptionAcademicMentoringDisplayVideo extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase database;
    Toolbar toolbar;
    ClassYoutube classVideo;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<ClassYoutube, YoutubeAdapter> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_display_video);

        recyclerView = findViewById(R.id.recyclerview_ShowVideo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.search_menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        classVideo = new ClassYoutube();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("AcademicMentoringVideos");

        FirebaseRecyclerOptions<ClassYoutube> options =
                new FirebaseRecyclerOptions.Builder<ClassYoutube>()
                        .setQuery(databaseReference, ClassYoutube.class)
                        .build();

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClassYoutube, YoutubeAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull YoutubeAdapter holder, int position, @NonNull ClassYoutube model) {
                holder.setYoutube(model.getVideoId(), model.getTitle(), model.getImageUrl());
                holder.setOnClicklistener(new YoutubeAdapter.Clicklistener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
                dialog.dismiss();
            }

            @NonNull
            @Override
            public YoutubeAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.youtube_row_item, parent, false);
                return new YoutubeAdapter(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_firebase);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void firebaseSearch(String searchtext) {
        String query = searchtext.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ClassYoutube> options =
                new FirebaseRecyclerOptions.Builder<ClassYoutube>()
                        .setQuery(firebaseQuery, ClassYoutube.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ClassYoutube, YoutubeAdapter>(options) {

                    @NonNull
                    @Override
                    public YoutubeAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.youtube_row_item, parent, false);

                        return new YoutubeAdapter(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull YoutubeAdapter holder, int position, @NonNull ClassYoutube model) {
                        holder.setYoutube(model.getVideoId(), model.getTitle(), model.getImageUrl());
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}