package com.BloomingSouls;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

public class ActivityNotificationsUser extends BaseBottomNavigationActivityUser {
    EditText searchText;
    DatabaseReference mDatabaseReference;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    List<ClassNotification> mData;
    LinearLayoutManager linearLayoutManager;

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

        recyclerView = findViewById(R.id.news_rv);
        searchText = findViewById(R.id.search_input);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Notifications");
        mData = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ClassNotification classNotification = postSnapshot.getValue(ClassNotification.class);
                    mData.add(classNotification);
                }
                notificationAdapter = new NotificationAdapter(ActivityNotificationsUser.this, mData);
                recyclerView.setAdapter(notificationAdapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notificationAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed() {
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

    @Override
    int getLayoutId() {
        return R.layout.notifications_user;
    }

    @Override
    int getBottomNavigationMenuItemId() {
        return R.id.navigation_notifications;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }
}
