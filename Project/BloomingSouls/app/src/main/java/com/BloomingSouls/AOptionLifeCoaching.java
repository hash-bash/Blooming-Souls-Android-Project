package com.BloomingSouls;

import android.content.Intent;
import android.os.Bundle;

import com.ncorti.slidetoact.SlideToActView;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AOptionLifeCoaching extends AppCompatActivity {
    NightModeSharedPref sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new NightModeSharedPref(this);
        if (sharedpref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ao_life_coaching);

        final SlideToActView slide1, slide2, slide3;

        slide1 = findViewById(R.id.post_content);
        slide2 = findViewById(R.id.post_video);
        slide3 = findViewById(R.id.post_pdf);

        slide1.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                Intent intent = new Intent(AOptionLifeCoaching.this, AOptionLifeCoachingPostContent.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                slide1.resetSlider();
            }
        });

        slide2.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                Intent intent = new Intent(AOptionLifeCoaching.this, AOptionLifeCoachingPostVideo.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                slide2.resetSlider();
            }
        });

        slide3.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                Intent intent = new Intent(AOptionLifeCoaching.this, AOptionLifeCoachingPostPdf.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                slide3.resetSlider();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
