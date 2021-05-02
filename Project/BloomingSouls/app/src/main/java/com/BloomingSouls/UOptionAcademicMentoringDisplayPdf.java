package com.BloomingSouls;

import android.annotation.SuppressLint;
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

public class UOptionAcademicMentoringDisplayPdf extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RecyclerView recyclerView;
    DatabaseReference mDatabaseReference;
    NightModeSharedPref sharedpref;
    FirebaseRecyclerAdapter<ClassPdf, PdfAdapter> firebaseRecyclerAdapter;

    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new NightModeSharedPref(this);
        if (sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_display_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("AcademicMentoringPdf");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerview_ShowPdf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ClassPdf> options =
                new FirebaseRecyclerOptions.Builder<ClassPdf>()
                        .setQuery(mDatabaseReference, ClassPdf.class)
                        .build();

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ClassPdf, PdfAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PdfAdapter holder, int position, @NonNull ClassPdf model) {
                holder.setPdf(model.getPdfUrl(), model.getName(), model.getImageUrl());
                holder.setOnClicklistener(new PdfAdapter.Clicklistener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                });
                dialog.dismiss();
            }

            @NonNull
            @Override
            public PdfAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pdf_row_item, parent, false);
                return new PdfAdapter(view);
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    private void firebaseSearch(String searchtext) {
        String query = searchtext.toLowerCase();
        Query firebaseQuery = mDatabaseReference.orderByChild("search").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ClassPdf> options =
                new FirebaseRecyclerOptions.Builder<ClassPdf>()
                        .setQuery(firebaseQuery, ClassPdf.class)
                        .build();

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ClassPdf, PdfAdapter>(options) {

                    @NonNull
                    @Override
                    public PdfAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.pdf_row_item, parent, false);

                        return new PdfAdapter(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull PdfAdapter holder, int position, @NonNull ClassPdf model) {
                        holder.setPdf(model.getPdfUrl(), model.getName(), model.getImageUrl());
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}