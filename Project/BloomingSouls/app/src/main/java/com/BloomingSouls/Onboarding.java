package com.BloomingSouls;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class Onboarding extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(Onboarding.this, "Registration Successful", Toast.LENGTH_SHORT).show();

        int iconH = 0, iconW = 0, marginTop = 0;

        Display mDisplay = getWindow().getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();
        int Height = mDisplay.getHeight();

        if (Height <= 1280) {
            iconH = 550;
            iconW = 500;
            marginTop = 0;
        } else if (Height > 1280 && Height < 1440) {
            iconH = 550;
            iconW = 500;
            marginTop = 200;
        } else {
            iconH = 850;
            iconW = 700;
            marginTop = 200;
        }

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Academics", "Topic wise descriptive videos, notes and proper guidance for academic exams as well as all entrance exams.", R.drawable.illustration2);
        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard1.setTitleColor(R.color.Black_Eel);
        ahoyOnboarderCard1.setDescriptionColor(R.color.Carbon_Gray);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard1.setIconLayoutParams(iconW, iconH, marginTop, 40, 40, 0);

        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Career Guidance", "Career assessment and counselling, and extensive support from the experts for focused career route.", R.drawable.illustration4);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setTitleColor(R.color.Black_Eel);
        ahoyOnboarderCard2.setDescriptionColor(R.color.Carbon_Gray);
        ahoyOnboarderCard2.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard2.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard2.setIconLayoutParams(iconW, iconH, marginTop, 40, 40, 0);

        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Personality Development", "Enhance thinking, feeling and behavioral skills by developing emotional as well as spiritual quotient.", R.drawable.illustration1);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setTitleColor(R.color.Black_Eel);
        ahoyOnboarderCard3.setDescriptionColor(R.color.Carbon_Gray);
        ahoyOnboarderCard3.setTitleTextSize(dpToPixels(7, this));
        ahoyOnboarderCard3.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard3.setIconLayoutParams(iconW, iconH, marginTop, 40, 40, 0);

        AhoyOnboarderCard ahoyOnboarderCard4 = new AhoyOnboarderCard("Life Coaching", "Mentors to guide the way to grow your life in right direction by building focus and concentration.", R.drawable.illustration3);
        ahoyOnboarderCard4.setBackgroundColor(R.color.white);
        ahoyOnboarderCard4.setTitleColor(R.color.Black_Eel);
        ahoyOnboarderCard4.setDescriptionColor(R.color.Carbon_Gray);
        ahoyOnboarderCard4.setTitleTextSize(dpToPixels(8, this));
        ahoyOnboarderCard4.setDescriptionTextSize(dpToPixels(6, this));
        ahoyOnboarderCard4.setIconLayoutParams(iconW, iconH, marginTop, 40, 40, 0);

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);
        pages.add(ahoyOnboarderCard4);

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.Blush_Red);
        colorList.add(R.color.Silk_Blue);
        colorList.add(R.color.Zombie_Green);
        colorList.add(R.color.Macaroni_and_Cheese);

        setColorBackground(colorList);
        setOnboardPages(pages);
        setInactiveIndicatorColor(R.color.BlackTrans);
        setActiveIndicatorColor(R.color.white);
        setFinishButtonTitle("Get Started");
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.rounded_button));
    }

    @Override
    public void onFinishButtonPressed() {
        Intent intent = new Intent(this, ActivityHomeUser.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
