package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.manasi.nearesto.helper.MenuNavigation;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        new MenuNavigation(this);
    }
}