package com.BloomingSouls;

import android.content.Context;
import android.content.SharedPreferences;

public class NightModeSharedPref {
    SharedPreferences mySharedPref;

    public NightModeSharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("NightModeSharedPref", Context.MODE_PRIVATE);
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    public Boolean loadNightModeState() {
        return (mySharedPref.getBoolean("NightMode", false));
    }
}