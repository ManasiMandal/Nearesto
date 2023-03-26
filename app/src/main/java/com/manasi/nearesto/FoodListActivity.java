package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manasi.nearesto.helper.MenuNavigation;
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

        db.collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<FoodItem> foodItems = task.getResult().toObjects(FoodItem.class);
                        setFoodItemsInContainer(foodItems);
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setFoodItemsInContainer(List<FoodItem> foodItems) {

        foodItemsContainer.removeAllViews();

        for (FoodItem foodItem: foodItems) {

            RelativeLayout foodItemCard = (RelativeLayout) getLayoutInflater().inflate(R.layout.food_item_card, null);
            ImageView ivFoodImage = foodItemCard.findViewById(R.id.iv_image);
            TextView tvName = foodItemCard.findViewById(R.id.tv_name);
            TextView tvRestaurantName = foodItemCard.findViewById(R.id.tv_restaurant_name);
            TextView tvPrice = foodItemCard.findViewById(R.id.tv_price);
//            RatingBar rbRating = foodItemCard.findViewById(R.id.rb_rating);
//            TextView tvRating = foodItemCard.findViewById(R.id.tv_rating);
            ImageView ivType = foodItemCard.findViewById(R.id.iv_type);

            fetchRestaurantsName(foodItem.getRestaurant(), tvRestaurantName);

            tvName.setText(foodItem.getName());
            tvPrice.setText("" + foodItem.getPrice());
//            rbRating.setRating(foodItem.getRating());
//            tvRating.setText("" + foodItem.getRating());
            if (foodItem.isNonVeg()) {
                ivType.setBackgroundResource(R.drawable.non_veg);
            }
//            foodItemCard.setOnClickListener(v -> {
//                Intent i = new Intent(FoodListActivity.this, ViewItem.class);
//                i.putExtra("food_item_id", foodItem.getId());
//                i.putExtra("food_item", foodItem);
//                startActivity(i);
//            });

            String url = foodItem.getUrl();

            if (url != null) {
                Picasso.get().load(url).into(ivFoodImage);
            }

            foodItemsContainer.addView(foodItemCard);
        }
    }

    private void fetchRestaurantsName(long restaurantId, TextView tvRestaurantName) {
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