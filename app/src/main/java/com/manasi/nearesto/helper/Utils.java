package com.manasi.nearesto.helper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manasi.nearesto.R;
import com.manasi.nearesto.ViewFoodItem;
import com.manasi.nearesto.ViewRestaurant;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;
import com.manasi.nearesto.modal.User;
import com.squareup.picasso.Picasso;

public class Utils {

    public final static String LOGIN_SHARED_FILE       = "login_preferences";
    public final static String RECENT_USER_SHARED_FILE = "recent_user";

    public final static String CART_ITEMS_SHARED_FILE  = "recent_user";
    public static ProgressDialog progressDialog(Activity activity, String msg) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setTitle("Nearesto");
        pd.setIcon(R.drawable.ic_nearesto);
        pd.setMessage(msg);
        pd.setCancelable(false);
        return pd;
    }

    public static void startActivity(Activity activity, Class destination) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }

    public static String getID(String email) {
        return email.replace('.', '_');
    }

    public static void setSharedPreferences(Activity activity, String sharedFile, User user) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(sharedFile, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhone());
        editor.putString("password", user.getPassword());
        editor.putString("profile_url", user.getProfile_url());
        editor.apply();
    }

    public static void addUserToSharedPreferences(Activity activity, User user) {
        setSharedPreferences(activity, LOGIN_SHARED_FILE, user);
    }

    public static void addRecentUserToSharedPreferences(Activity activity, User user) {
        setSharedPreferences(activity, RECENT_USER_SHARED_FILE, user);
    }

    public static void clearLoginPreferences(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(LOGIN_SHARED_FILE, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static User getUserFromIntent(Activity activity) {
        User user = new User();
        user.setEmail(activity.getIntent().getStringExtra("email"));
        user.setName(activity.getIntent().getStringExtra("name"));
        user.setPhone(activity.getIntent().getStringExtra("phone"));
        user.setProfile_url(activity.getIntent().getStringExtra("profile_url"));
        return user;
    }

    public static Intent addUserToIntent(Intent intent, User user) {
        intent.putExtra("profile_url", user.getProfile_url());
        intent.putExtra("name", user.getName());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("phone", user.getPhone());
        return intent;
    }

    public static LinearLayout prepareRestaurantCard(Activity activity, Restaurant restaurant, Boolean single) {
        LinearLayout restaurantItemCard = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.restaurant_item_card, null);

        ImageView ivRestaurantImage = restaurantItemCard.findViewById(R.id.iv_restaurant_image);
//            int size = Resources.getSystem().getDisplayMetrics().widthPixels - (cardContainer.getPaddingRight() * 2);
//            ivRestaurantImage.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        TextView tvName             = restaurantItemCard.findViewById(R.id.tv_name);
        TextView tvDescription      = restaurantItemCard.findViewById(R.id.tv_description);
        TextView tvLocation         = restaurantItemCard.findViewById(R.id.tv_location);
        RatingBar rbRating          = restaurantItemCard.findViewById(R.id.rb_rating);
        TextView tvRating           = restaurantItemCard.findViewById(R.id.tv_rating);
        TextView tvDistance         = restaurantItemCard.findViewById(R.id.tv_distance);
        ImageView ivTypeVeg         = restaurantItemCard.findViewById(R.id.iv_type_veg);
        ImageView ivTypeNonVeg      = restaurantItemCard.findViewById(R.id.iv_type_non_veg);

        tvName.setBackgroundColor(Color.WHITE);
        tvDescription.setBackgroundColor(Color.WHITE);
        tvLocation.setBackgroundColor(Color.WHITE);
        tvDistance.setBackgroundColor(Color.WHITE);

        tvName.setText(restaurant.getName());
        tvDescription.setText(restaurant.getDescription());
        tvLocation.setText(restaurant.getFullAddress());
        if ( ! single ) {
            tvDescription.setVisibility(View.GONE);
            tvLocation.setText(restaurant.getAddress2());
        }
        rbRating.setRating(restaurant.getRating());
        tvRating.setText("" + restaurant.getRating());

        if (restaurant.getType() == 0) {
            ivTypeNonVeg.setVisibility(View.GONE);
            // Positioning veg type image view
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivTypeVeg.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            ivTypeVeg.setLayoutParams(params);
        }
        if (restaurant.getType() == 1) {
            ivTypeVeg.setVisibility(View.GONE);
        }
        restaurantItemCard.setOnClickListener(v -> {
            Intent i = new Intent(activity, ViewRestaurant.class);
            i.putExtra("restaurant_id", restaurant.getId());
            i.putExtra("restaurant", restaurant);
            activity.startActivity(i);
        });

        String url = restaurant.getUrl();

        if (url != null) {
            Picasso.get().load(url).into(ivRestaurantImage);
        }

        return restaurantItemCard;
    }

    public static RelativeLayout prepareFoodItemCard(Activity activity, FoodItem foodItem) {

        RelativeLayout foodItemCard = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.food_item_card, null);

        ImageView ivFoodImage     = foodItemCard.findViewById(R.id.iv_image);
        TextView tvName           = foodItemCard.findViewById(R.id.tv_name);
        TextView tvRestaurantName = foodItemCard.findViewById(R.id.tv_restaurant_name);
        TextView tvPrice          = foodItemCard.findViewById(R.id.tv_price);
        ImageView ivType          = foodItemCard.findViewById(R.id.iv_type);

        tvName.setBackgroundColor(Color.WHITE);
        tvRestaurantName.setBackgroundColor(Color.WHITE);
        tvPrice.setBackgroundColor(Color.WHITE);

        tvName.setText(foodItem.getName());
        tvRestaurantName.setText(foodItem.getRestaurantName());
        tvPrice.setText("₹ " + foodItem.getPrice());

        if (foodItem.isNonVeg()) {
            ivType.setBackgroundResource(R.drawable.non_veg);
        } else {
            ivType.setBackgroundResource(R.drawable.veg);
        }
        foodItemCard.setOnClickListener(v -> {
            Intent i = new Intent(activity, ViewFoodItem.class);
            i.putExtra("restaurant_id", foodItem.getRestaurant());
            i.putExtra("food_item", foodItem);
            activity.startActivity(i);
        });

        String url = foodItem.getUrl();

        if (url != null) {
            Picasso.get().load(url).into(ivFoodImage);
        }

        return foodItemCard;
    }
}
