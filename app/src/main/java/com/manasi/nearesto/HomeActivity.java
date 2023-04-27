package com.manasi.nearesto;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.manasi.nearesto.helper.CSVDataImporter;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.Restaurant;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout restaurantsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        getSupportActionBar().hide();
        new MenuNavigation(this);

        restaurantsContainer = findViewById(R.id.restaurants_container);

        db = FirebaseFirestore.getInstance();
        loadRestaurants();

        EditText locationSearch = findViewById(R.id.et_location_search);
        locationSearch.setOnTouchListener((view, motionEvent) -> {
            Intent i = new Intent(HomeActivity.this, MapsActivity.class);
            i.putExtra("location",locationSearch.getText().toString());
            startActivity(i);
            return true;
        });
//        locationSearch.setOnClickListener(view -> {
//            Intent i = new Intent(HomeActivity.this, MapsActivity.class);
//            i.putExtra("location",locationSearch.getText().toString());
//            startActivity(i);
//        });

        Utils.setupCartMenu(this);

        // Listener for button to import restaurants data from the CSV file present in raw folder
        Button btnImportRestaurants = findViewById(R.id.btn_import_restaurants);
        btnImportRestaurants.setOnClickListener(view -> {
            try {
                CSVDataImporter.importRestaurants(this);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Listener for button to import food items data from the CSV file present in raw folder
        Button btnImportFoodItems = findViewById(R.id.btn_import_food_items);
        btnImportFoodItems.setOnClickListener(view -> {
            try {
                CSVDataImporter.importFoodItems(this);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadRestaurants() {

        ProgressDialog loader = Utils.progressDialog(this, "Loading restaurants...");
        loader.show();

        db.collection("restaurants")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<Restaurant> restaurants = task.getResult().toObjects(Restaurant.class);
                        setRestaurantsInContainer(restaurants);
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                });
    }

    private void setRestaurantsInContainer(List<Restaurant> restaurants) {
        restaurantsContainer.removeAllViews();
        for (Restaurant restaurant: restaurants) {
            LinearLayout restaurantItemCard = Utils.prepareRestaurantCard(this, restaurant, false);
            restaurantsContainer.addView(restaurantItemCard);
        }
    }

    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ActivityCompat.finishAffinity(this);
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}

