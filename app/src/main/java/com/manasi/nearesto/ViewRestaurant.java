package com.manasi.nearesto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewRestaurant extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout foodItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_restaurant);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        foodItemsContainer = findViewById(R.id.food_items_container);
        db = FirebaseFirestore.getInstance();

//        Bundle extras = getIntent().getExtras();
//        long restaurantId = extras.getLong("restaurant_id");
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        setRestaurantDetailsInCard(restaurant);
        setRestaurantFoodItemsInCard(restaurant);
    }

    private void setRestaurantDetailsInCard(Restaurant restaurant) {
        LinearLayout detailsContainer   = findViewById(R.id.details_container);
        LinearLayout restaurantItemCard = Utils.prepareRestaurantCard(this, restaurant, true);
        detailsContainer.removeViewAt(0);
        detailsContainer.addView(restaurantItemCard, 0);
    }

    private void setRestaurantFoodItemsInCard(Restaurant restaurant) {
        ProgressDialog loader = Utils.progressDialog(this, "Loading restaurants...");
        loader.show();

        db.collection("items")
                .whereEqualTo("restaurant", restaurant.getId())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<FoodItem> foodItems = task.getResult().toObjects(FoodItem.class);
                        setFoodItemsInContainer(foodItems, restaurant);
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

    private void setFoodItemsInContainer(List<FoodItem> foodItems, Restaurant restaurant) {
        foodItemsContainer.removeAllViews();
        for (FoodItem foodItem: foodItems) {
            RelativeLayout foodItemCard = Utils.prepareFoodItemCard(this, foodItem);
            foodItemsContainer.addView(foodItemCard);
        }
    }
}
