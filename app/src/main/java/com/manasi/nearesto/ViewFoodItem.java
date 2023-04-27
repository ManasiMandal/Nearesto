package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.CartItem;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewFoodItem extends AppCompatActivity {

    private FirebaseFirestore db;

    private Button btnAddToCart, btnDecrease, btnIncrease;
    private EditText etQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food_item);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> finish() );
        Utils.setupCartMenu(this);

        db = FirebaseFirestore.getInstance();

        FoodItem foodItem = (FoodItem) getIntent().getSerializableExtra("food_item");
        Bundle extras     = getIntent().getExtras();
        long restaurantId = extras.getLong("restaurant_id");

        loadFoodItemData(foodItem);
        loadRestaurantData(restaurantId);

        etQuantity  = findViewById(R.id.et_quantity);
        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);

        btnDecrease.setOnClickListener(view -> {
            int quantity = Integer.parseInt("0" + etQuantity.getText().toString());
            if( quantity > 1 ) {
                etQuantity.setText("" + --quantity);
            }
        });

        btnIncrease.setOnClickListener(view -> {
            int quantity = Integer.parseInt("0" + etQuantity.getText().toString());
            if( quantity < 100 ) {
                etQuantity.setText("" + ++quantity);
            }
        });

        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(view -> {
            int quantity = Integer.parseInt("0" + etQuantity.getText().toString());
            if(quantity < 1) {
                Toast.makeText(this, "Minimum 1 quantity required!", Toast.LENGTH_SHORT).show();
                return;
            }
            CartItem cartItem = new CartItem(foodItem, quantity);
            Utils.addItemToCart(this, cartItem, false);
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
        tvKeywords.setText(foodItem.getKeywords().replaceAll("\\|", ", "));

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