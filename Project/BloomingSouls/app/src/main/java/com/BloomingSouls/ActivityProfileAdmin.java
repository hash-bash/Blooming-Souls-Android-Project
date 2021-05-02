package com.BloomingSouls;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class ActivityProfileAdmin extends BaseBottomNavigationActivityAdmin {
    TextView tvPhone, tvMail, tvMessage;
    TextView tvCourse, tvMemSince, tvPoints;
    TextView tvName;
    CircleImageView profilePic;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    LinearLayout l1, l2, l3;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvPhone = findViewById(R.id.tv_phone);
        tvCourse = findViewById(R.id.tv_course_com);
        tvMail = findViewById(R.id.tv_email);
        tvMemSince = findViewById(R.id.tv_mem_since);
        tvMessage = findViewById(R.id.tv_message);
        tvPoints = findViewById(R.id.tv_points);
        tvName = findViewById(R.id.tv_name);
        profilePic = findViewById(R.id.circular_img);
        l1 = findViewById(R.id.l1);
        l2 = findViewById(R.id.l2);
        l3 = findViewById(R.id.l3);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        AlertDialog dialog = new SpotsDialog.Builder().setContext(this).build();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_white_card);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserData");
        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userPhone = dataSnapshot.child("phone").getValue(String.class);
                String userEmail = dataSnapshot.child("email").getValue(String.class);
                String userName = dataSnapshot.child("name").getValue(String.class);
                int userPoints = dataSnapshot.child("points").getValue(int.class);
                int userCourses = dataSnapshot.child("courses").getValue(int.class);
                String userMemberS = dataSnapshot.child("joined").getValue(String.class);

                tvName.setText(userName);
                tvPoints.setText(Integer.toString(userPoints));
                tvCourse.setText(Integer.toString(userCourses));
                tvMemSince.setText(userMemberS);

                if (userEmail != null) {
                    tvMail.setText(userEmail);
                } else {
                    l1.setVisibility(View.GONE);
                }
                if (userPhone != null) {
                    tvMessage.setText(userPhone);
                    tvPhone.setText(userPhone);
                } else {
                    l2.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Glide.with(this).load(currentUser.getPhotoUrl()).into(profilePic);
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
        return R.layout.profile_user;
    }

    @Override
    int getBottomNavigationMenuItemId1() {
        return R.id.navigation_profile;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }
}
