package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.manasi.nearesto.helper.MenuNavigation;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        new MenuNavigation(this);
    }
}