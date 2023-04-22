package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout foodItemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        new MenuNavigation(this);

        foodItemsContainer = findViewById(R.id.food_items_container);

        db = FirebaseFirestore.getInstance();
        loadFoodItems();
    }

    private void loadFoodItems() {

        ProgressDialog loader = Utils.progressDialog(this, "Loading items...");
        loader.show();

        db.collection("items")
                .orderBy("restaurant", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<FoodItem> foodItems = task.getResult().toObjects(FoodItem.class);
                        setFoodItemsInContainer(foodItems);
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

    private void setFoodItemsInContainer(List<FoodItem> foodItems) {
        foodItemsContainer.removeAllViews();
        for (FoodItem foodItem: foodItems) {
            RelativeLayout foodItemCard = Utils.prepareFoodItemCard(this, foodItem);
            foodItemsContainer.addView(foodItemCard);
        }
    }

    private void fetchRestaurantName(long restaurantId, TextView tvRestaurantName) {
        db.collection("restaurants")
                .whereEqualTo("id", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            String restaurantName =  restaurant.getName();
                            tvRestaurantName.setText(restaurantName);
                        }
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}