package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.CSVDataImporter;
import com.manasi.nearesto.helper.MenuNavigation;
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



        Button btnImportRestaurants = findViewById(R.id.btn_import_restaurants);
        btnImportRestaurants.setOnClickListener(view -> {
            try {

                CSVDataImporter.doImport(this, R.raw.nearesto_food_items, "items", "id");
            } catch (IOException e) {

                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadRestaurants() {

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d("mydata", task.getResult().toString());
                        List<Restaurant> restaurants = task.getResult().toObjects(Restaurant.class);
                        setRestaurantsInContainer(restaurants);
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setRestaurantsInContainer(List<Restaurant> restaurants) {

        restaurantsContainer.removeAllViews();

        for (Restaurant restaurant: restaurants) {



            LinearLayout restaurantItemCard = (LinearLayout) getLayoutInflater().inflate(R.layout.restaurant_item_card, null);
            ImageView ivRestaurantImage = restaurantItemCard.findViewById(R.id.iv_restaurant_image);
//            int size = Resources.getSystem().getDisplayMetrics().widthPixels - (cardContainer.getPaddingRight() * 2);
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            TextView tvName = restaurantItemCard.findViewById(R.id.tv_name);
            TextView tvLocation = restaurantItemCard.findViewById(R.id.tv_location);
            RatingBar rbRating = restaurantItemCard.findViewById(R.id.rb_rating);
            TextView tvRating = restaurantItemCard.findViewById(R.id.tv_rating);
            TextView tvDistance = restaurantItemCard.findViewById(R.id.tv_distance);
            ImageView ivTypeVeg = restaurantItemCard.findViewById(R.id.iv_type_veg);
            ImageView ivTypeNonVeg = restaurantItemCard.findViewById(R.id.iv_type_non_veg);

            tvName.setText(restaurant.getName());
            tvLocation.setText(restaurant.getAddress2());
            rbRating.setRating(restaurant.getRating());
            tvRating.setText("" + restaurant.getRating());
//            tvDistance.setText(restaurant.getLatitude() + ", " + restaurant.getLongitude()) ;

            if (restaurant.getType() == 0) {
                ivTypeNonVeg.setVisibility(View.GONE);
                // Create the LayoutParams
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivTypeVeg.getLayoutParams();
                // Add all the rules you need
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                // Once you are done set the LayoutParams to the layout
                ivTypeVeg.setLayoutParams(params);
            }
            if (restaurant.getType() == 1) {
                ivTypeVeg.setVisibility(View.GONE);
            }
            restaurantItemCard.setOnClickListener(v -> {
                Intent i = new Intent(HomeActivity.this, ViewRestaurant.class);
                i.putExtra("restaurant_id", restaurant.getId());
                i.putExtra("restaurant", restaurant);
                startActivity(i);
            });

            String url = restaurant.getUrl();

            if (url != null) {
                Picasso.get().load(url).into(ivRestaurantImage);
            }

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

