package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.modal.Restaurant;

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
    }

    private void loadRestaurants() {

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
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
//            ImageView imageView = card.findViewById(R.id.iv_home_img);
//            int size = Resources.getSystem().getDisplayMetrics().widthPixels - (cardContainer.getPaddingRight() * 2);
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
//            ImageView ivProfile = card.findViewById(R.id.iv_profile);
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

//            Picasso.get().load(image.getUrl()).into(imageView);
//
//            image.getUser()
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        User user = task.getResult().toObject(User.class);
//                        Picasso.get().load(user.getProfile_url()).into(ivProfile);
//                        card.setOnClickListener(view -> viewImage(image.getUrl(), user));
//                    })
//                    .addOnFailureListener(ex -> Utils.toast(this, ex.getMessage()));

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

