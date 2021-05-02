package com.BloomingSouls;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OfflineAlertIntent extends AppCompatActivity implements OfflineConnectivityReceiver.ConnectivityReceiverListener {
    Button button;
    IntentFilter intentFilter;
    OfflineConnectivityReceiver offlineConnectivityReceiver;
    String ActivityName;
    Bundle extras;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_alert);

        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        offlineConnectivityReceiver = new OfflineConnectivityReceiver();

        extras = getIntent().getExtras();
        if (extras != null)
            ActivityName = getIntent().getStringExtra("key");

        button = findViewById(R.id.btnOff);
        progress = findViewById(R.id.progressBar);

        button.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    button.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);

                    checkInternetConnection();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogBox);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.logo_icon);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
        registerReceiver(offlineConnectivityReceiver, intentFilter);
        unregisterReceiver(offlineConnectivityReceiver);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            finish();
            Toast.makeText(this, "Connection Successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkInternetConnection() throws ClassNotFoundException {
        boolean isConnected = OfflineConnectivityReceiver.isConnected();
        if (isConnected) {
            changeActivity();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                }
            }, 400);
        } else {
            Toast.makeText(this, "You're Offline", Toast.LENGTH_SHORT).show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);
                }
            }, 400);
        }
    }

    private void changeActivity() throws ClassNotFoundException {
        if (extras != null) {
            startActivity(new Intent(OfflineAlertIntent.this, Class.forName(ActivityName)));
            finish();
        }
    }
}
