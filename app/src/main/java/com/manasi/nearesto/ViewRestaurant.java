package com.manasi.nearesto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
//        Toast.makeText(this, restaurantId+"", Toast.LENGTH_SHORT).show();
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
//            Toast.makeText(this, restaurant.getName(), Toast.LENGTH_SHORT).show();

        setRestaurantDetailsInCard(restaurant);
        setRestaurantFoodItemsInCard(restaurant);
    }

    private void setRestaurantDetailsInCard(Restaurant restaurant) {
        LinearLayout restaurantItemCard = findViewById(R.id.restaurant_details_container);

        ImageView ivImage = restaurantItemCard.findViewById(R.id.iv_restaurant_image);
        TextView tvName = restaurantItemCard.findViewById(R.id.tv_name);
        TextView tvLocation = restaurantItemCard.findViewById(R.id.tv_location);
        RatingBar rbRating = restaurantItemCard.findViewById(R.id.rb_rating);
        TextView tvRating = restaurantItemCard.findViewById(R.id.tv_rating);
        TextView tvDistance = restaurantItemCard.findViewById(R.id.tv_distance);
        ImageView ivTypeVeg = restaurantItemCard.findViewById(R.id.iv_type_veg);
        ImageView ivTypeNonVeg = restaurantItemCard.findViewById(R.id.iv_type_non_veg);

        ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tvName.setText(restaurant.getName());
        tvLocation.setText(restaurant.getAddress2());
        rbRating.setRating(restaurant.getRating());
        tvRating.setText("" + restaurant.getRating());
        tvDistance.setText(restaurant.getLatitude() + ", " + restaurant.getLongitude()) ;
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
    }

    private void setRestaurantFoodItemsInCard(Restaurant restaurant) {
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
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setFoodItemsInContainer(List<FoodItem> foodItems, Restaurant restaurant) {

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

            tvName.setText(foodItem.getName());
            tvRestaurantName.setText(restaurant.getName());
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
}
