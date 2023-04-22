package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewFoodItem extends AppCompatActivity {

    private FirebaseFirestore db;

    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food_item);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        db = FirebaseFirestore.getInstance();

        FoodItem foodItem = (FoodItem) getIntent().getSerializableExtra("food_item");
        Bundle extras     = getIntent().getExtras();
        long restaurantId = extras.getLong("restaurant_id");

        loadFoodItemData(foodItem);
        loadRestaurantData(restaurantId);

        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(view -> {
            Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFoodItemData(FoodItem foodItem) {
        ImageView ivFoodImage  = findViewById(R.id.iv_restaurant_image);
        TextView tvName        = findViewById(R.id.tv_name);
        ImageView ivType       = findViewById(R.id.iv_type);
        TextView tvDescription = findViewById(R.id.tv_description);
        TextView tvPrice       = findViewById(R.id.tv_price);
        RatingBar rbRating     = findViewById(R.id.rb_rating);
        TextView tvRating      = findViewById(R.id.tv_rating);
        TextView tvKeywords    = findViewById(R.id.tv_keywords);

        String url = foodItem.getUrl();
        if (url != null) {
            Picasso.get().load(url).into(ivFoodImage);
        }

        tvName.setBackgroundColor(Color.WHITE);
        tvDescription.setBackgroundColor(Color.WHITE);
        tvPrice.setBackgroundColor(Color.WHITE);
        tvKeywords.setBackgroundColor(Color.WHITE);

        tvName.setText(foodItem.getName());
        tvDescription.setText(foodItem.getDescription());
        tvPrice.setText("₹ " + foodItem.getPrice());
        rbRating.setRating(foodItem.getRating());
        tvRating.setText("" + foodItem.getRating());
        tvKeywords.setText(foodItem.getKeywords());

        if (foodItem.isNonVeg()) {
            ivType.setBackgroundResource(R.drawable.non_veg);
        } else {
            ivType.setBackgroundResource(R.drawable.veg);
        }
    }

    private void loadRestaurantData(long restaurantId) {
        ProgressDialog loader = Utils.progressDialog(this, "Loading restaurants...");
        loader.show();

        db.collection("restaurants")
                .whereEqualTo("id", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        LinearLayout detailsContainer = findViewById(R.id.restaurant_details_container);
                        LinearLayout cardsContainer   = findViewById(R.id.cards_container);
                        cardsContainer.removeView(detailsContainer);
                        List<Restaurant> restaurants  = task.getResult().toObjects(Restaurant.class);
                        if(restaurants.get(0) != null) {
                            LinearLayout restaurantItemCard = Utils.prepareRestaurantCard(this, restaurants.get(0), true);
                            cardsContainer.addView(restaurantItemCard);
                        }
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

}