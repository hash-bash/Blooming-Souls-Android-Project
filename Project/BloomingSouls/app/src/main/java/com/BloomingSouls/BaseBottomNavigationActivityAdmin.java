package com.BloomingSouls;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseBottomNavigationActivityAdmin extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    protected BottomNavigationView BNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId1());

        BNavigationView = findViewById(R.id.navigation);
        BNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        BNavigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(BaseBottomNavigationActivityAdmin.this, ActivityHomeAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (itemId == R.id.navigation_profile) {
                    startActivity(new Intent(BaseBottomNavigationActivityAdmin.this, ActivityProfileAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (itemId == R.id.navigation_notifications) {
                    startActivity(new Intent(BaseBottomNavigationActivityAdmin.this, ActivityNotificationsAdmin.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (itemId == R.id.navigation_talk) {
                    Intent intToMain = new Intent(BaseBottomNavigationActivityAdmin.this, ActivityChatAdmin.class);
                    startActivity(intToMain);
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                }
            }
        }, 200);
        return true;
    }

    private void updateNavigationBarState() {
        int actionId = getBottomNavigationMenuItemId1();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = BNavigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getLayoutId1();

    abstract int getBottomNavigationMenuItemId1();
}