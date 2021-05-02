package com.BloomingSouls;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

public class ActivityHomeAdmin extends BaseBottomNavigationActivityAdmin implements NavigationView.OnNavigationItemSelectedListener, OfflineConnectivityReceiver.ConnectivityReceiverListener {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;
    NestedScrollView scrollView;
    IntentFilter intentFilter;
    OfflineConnectivityReceiver offlineConnectivityReceiver;
    CardView card1, card2, card3, card4, card5, card6;

    boolean doubleBackToExitPressedOnce = false;

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.welcomeText);
        scrollView = findViewById(R.id.scrollView);
        card1 = findViewById(R.id.cv1);
        card2 = findViewById(R.id.cv2);
        card3 = findViewById(R.id.cv3);
        card4 = findViewById(R.id.cv4);
        card5 = findViewById(R.id.cv5);
        card6 = findViewById(R.id.cv6);

        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        offlineConnectivityReceiver = new OfflineConnectivityReceiver();

        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                scrollView.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
                drawer.setScrimColor(Color.TRANSPARENT);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeAdmin.this, AOptionAcademicMentoring.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeAdmin.this, AOptionCareerGuidance.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeAdmin.this, AOptionMagnificentQuotes.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeAdmin.this, AOptionSeminarAndWorkshops.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeAdmin.this, AOptionLifeCoaching.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityHomeAdmin.this, AOptionPersonalityDevelopment.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int i = item.getItemId();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (i == R.id.nav_settings) {
                    Intent intToMain = new Intent(ActivityHomeAdmin.this, NavSettingsAdminActivity.class);
                    startActivity(intToMain);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (i == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(ActivityHomeAdmin.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                    Intent intToMain = new Intent(ActivityHomeAdmin.this, SigninUserActivity.class);
                    startActivity(intToMain);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                } else if (i == R.id.navigation_home) {
                    startActivity(new Intent(ActivityHomeAdmin.this, ActivityHomeAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (i == R.id.navigation_profile) {
                    startActivity(new Intent(ActivityHomeAdmin.this, ActivityProfileAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (i == R.id.navigation_notifications) {
                    startActivity(new Intent(ActivityHomeAdmin.this, ActivityNotificationsAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (i == R.id.navigation_talk) {
                    startActivity(new Intent(ActivityHomeAdmin.this, ActivityChatAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                }
            }
        }, 300);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finishAffinity();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(offlineConnectivityReceiver, intentFilter);
        BSApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(offlineConnectivityReceiver);
    }

    @Override
    int getLayoutId1() {
        return R.layout.home_admin;
    }

    @Override
    int getBottomNavigationMenuItemId1() {
        return R.id.navigation_home;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            changeActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }

    private void changeActivity() {
        String activityName = "ActivityHomeAdmin.class";
        Intent intent = new Intent(ActivityHomeAdmin.this, OfflineAlertIntent.class);
        intent.putExtra("key", activityName);
        startActivity(intent);
    }

    public void updateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.userName);
        TextView navUserMail = headerView.findViewById(R.id.userEmail);
        ImageView navUserPhoto = headerView.findViewById(R.id.userPhoto);

        Glide.with(this).load(currentUser.getPhotoUrl()).transition(GenericTransitionOptions.with(R.anim.an_fadein)).into(navUserPhoto);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserData");
        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                String userName = dataSnapshot.child("name").getValue(String.class);

                navUserMail.setText(userEmail);
                navUsername.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}