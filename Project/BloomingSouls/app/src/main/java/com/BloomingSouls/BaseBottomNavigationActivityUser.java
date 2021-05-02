package com.BloomingSouls;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseBottomNavigationActivityUser extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    protected BottomNavigationView BNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

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
                    startActivity(new Intent(BaseBottomNavigationActivityUser.this, ActivityHomeUser.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (itemId == R.id.navigation_profile) {
                    startActivity(new Intent(BaseBottomNavigationActivityUser.this, ActivityProfileUser.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (itemId == R.id.navigation_notifications) {
                    startActivity(new Intent(BaseBottomNavigationActivityUser.this, ActivityNotificationsUser.class));
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                } else if (itemId == R.id.navigation_talk) {
                    Intent intToMain = new Intent(BaseBottomNavigationActivityUser.this, ActivityChatUser.class);
                    startActivity(intToMain);
                    overridePendingTransition(R.anim.an_fadein, R.anim.an_zoomin);
                }
            }
        }, 200);
        return true;
    }

    private void updateNavigationBarState() {
        int actionId = getBottomNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = BNavigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getLayoutId();

    abstract int getBottomNavigationMenuItemId();
}