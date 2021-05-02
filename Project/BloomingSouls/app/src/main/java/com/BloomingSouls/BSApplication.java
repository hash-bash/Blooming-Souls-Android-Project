package com.BloomingSouls;

import android.app.Application;

public class BSApplication extends Application {
    private static BSApplication mInstance;

    public static synchronized BSApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public void setConnectivityListener(OfflineConnectivityReceiver.ConnectivityReceiverListener listener) {
        OfflineConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
