package com.BloomingSouls;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;

public class ActivityNotificationsAdmin extends BaseBottomNavigationActivityAdmin {
    TextView editTextTitle;
    TextView editTextMessage;
    DatabaseReference mDatabaseReference;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    List<ClassNotification> mData;
    Button send;
    LinearLayoutManager linearLayoutManager;
    ConstraintLayout rootLayout;
    ClassNotification deletedNotification = null;

    boolean doubleBackToExitPressedOnce = false;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                deletedNotification = mData.get(position);
                String[] uploads = new String[mData.size()];
                uploads[position] = mData.get(position).getTitle();

                Query applesQuery = mDatabaseReference.orderByChild("title").equalTo(uploads[position]);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                mData.remove(position);
                notificationAdapter.notifyItemRemoved(position);
                Snackbar.make(rootLayout, "Deleted Successfully: " + '"' + deletedNotification.getTitle() + '"', Snackbar.LENGTH_SHORT).show();
            }
        }
    };

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

        editTextMessage = findViewById(R.id.edit_text_message);
        editTextTitle = findViewById(R.id.edit_text_title);
        rootLayout = findViewById(R.id.root_layout);
        recyclerView = findViewById(R.id.news_rv);
        send = findViewById(R.id.send);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Notifications");
        mData = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
                notificationAdapter = new NotificationAdapter(ActivityNotificationsAdmin.this, mData);
                recyclerView.setAdapter(notificationAdapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM");
                String datetime = simpleDateFormat.format(calendar.getTime());

                ClassNotification classNotification = new ClassNotification(title, message, datetime);
                mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(classNotification);
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
    int getLayoutId1() {
        return R.layout.notifications_admin;
    }

    @Override
    int getBottomNavigationMenuItemId1() {
        return R.id.navigation_notifications;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }
}
