package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    // ImageView which holds the splash screen icon
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fully hides the screen notification bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Render (provides) the elements on the screen
        setContentView(R.layout.activity_splash);
        // hides the action bar
//        getSupportActionBar().hide();

        // fetch the xml element by id image
        image = findViewById(R.id.image);
        image.animate().alpha(0.6f).setDuration(2000);//2000 = 2 sec

        // wait for 3 sec and move to next screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}