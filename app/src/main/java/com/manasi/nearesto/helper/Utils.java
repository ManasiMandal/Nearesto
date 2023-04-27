package com.manasi.nearesto.helper;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manasi.nearesto.CartActivity;
import com.manasi.nearesto.R;
import com.manasi.nearesto.ViewFoodItem;
import com.manasi.nearesto.ViewOrder;
import com.manasi.nearesto.ViewRestaurant;
import com.manasi.nearesto.modal.CartItem;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Order;
import com.manasi.nearesto.modal.Restaurant;
import com.manasi.nearesto.modal.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {

    public final static String LOGIN_SHARED_FILE       = "login_preferences";
    public final static String RECENT_USER_SHARED_FILE = "recent_user";

    public final static String CART_ITEMS_SHARED_FILE  = "cart_items";

    public static void toast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog progressDialog(Activity activity, String msg) {
        ProgressDialog pd = new ProgressDialog(activity);
        pd.setTitle("Nearesto");
        pd.setIcon(R.drawable.ic_nearesto);
        pd.setMessage(msg);
        pd.setCancelable(false);
        return pd;
    }

    public static void startActivity(Activity activity, Class destination ) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }
    //To get the document id for the firbase firestore as it doesn't allow '.' in document id's
    public static String getID(String email) {
        return email.replace('.', '_');
    }


    public static ArrayList<CartItem> getCartItems(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(CART_ITEMS_SHARED_FILE, MODE_PRIVATE);

        // Get the saved JSON string for the object
        String json = sharedPreferences.getString("cart", null);

        ArrayList<CartItem> cartItems = new ArrayList<>();
        // If the JSON string is not null, convert it to the object type
        if (json != null) {

            //Get the data from json cart stream to cart item array object (casting)
            cartItems = new Gson().fromJson(json, new TypeToken<ArrayList<CartItem>>(){}.getType());
            return cartItems;
        }
        return cartItems;
    }

    public static void addItemToCart(Activity activity, CartItem cartItem, boolean isCart) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(CART_ITEMS_SHARED_FILE, MODE_PRIVATE).edit();
        ArrayList<CartItem> cartItems   = getCartItems(activity);
        boolean add = true;
        for(CartItem item : cartItems) {
            if(item.getFoodItem().getId() == cartItem.getFoodItem().getId()) {
                int quantity = item.getQuantity() + cartItem.getQuantity();
                if ( isCart ) {
                    quantity = cartItem.getQuantity();
                }
                item.setQuantity(quantity);
                add = false;
                if ( ! isCart ) {
                    Toast.makeText(activity, "Item updated in the cart!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        if(add) {
            cartItems.add(cartItem);
            Toast.makeText(activity, "Item added to the cart!", Toast.LENGTH_SHORT).show();
        }
        // Convert the object to a JSON string
        String json = new Gson().toJson(cartItems);
        editor.putString("cart", json);
        editor.apply();
    }

    public static void removeItemFromCart(Activity activity, CartItem cartItem) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(CART_ITEMS_SHARED_FILE, MODE_PRIVATE).edit();
        ArrayList<CartItem> cartItems   = getCartItems(activity);
        int index = 0;
        for(CartItem item : cartItems) {
            if(item.getFoodItem().getId() == cartItem.getFoodItem().getId()) {
                break;
            }
            index++;
        }
        cartItems.remove(index);
        String json = new Gson().toJson(cartItems);
        editor.putString("cart", json);
        editor.apply();
    }

    public static void clearCart(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(CART_ITEMS_SHARED_FILE, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    public static void setUserToSharedPreferences(Activity activity, String sharedFile, User user) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(sharedFile, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhone());
        editor.putString("password", user.getPassword());
        editor.putString("profile_url", user.getProfile_url());
        editor.apply();
    }

    public static void addUserToSharedPreferences(Activity activity, User user) {
        setUserToSharedPreferences(activity, LOGIN_SHARED_FILE, user);
}

    public static void addRecentUserToSharedPreferences(Activity activity, User user) {
        setUserToSharedPreferences(activity, RECENT_USER_SHARED_FILE, user);
    }

    public static User getUserFromSharedPreferences(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(LOGIN_SHARED_FILE, MODE_PRIVATE);
        User user = new User();
        user.setEmail(sp.getString("email", null));
        user.setName(sp.getString("name", null));
        user.setPhone(sp.getString("phone", null));
        return user;
    }

    public static void clearLoginPreferences(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(LOGIN_SHARED_FILE, MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }
    //
    public static LinearLayout prepareRestaurantCard(Activity activity, Restaurant restaurant, Boolean single) {
        LinearLayout restaurantItemCard = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.restaurant_item_card, null);

        ImageView ivRestaurantImage = restaurantItemCard.findViewById(R.id.iv_restaurant_image);
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

    public static RelativeLayout prepareOrderCard(Activity activity, Order order) {

        RelativeLayout orderCard = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.order_card, null);

        ImageView ivFoodImage   = orderCard.findViewById(R.id.iv_image);
        TextView tvOrderMessage = orderCard.findViewById(R.id.tv_order_message);
        TextView tvOrderDate    = orderCard.findViewById(R.id.tv_order_date);
        TextView tvOrderTotal   = orderCard.findViewById(R.id.tv_order_total);

        tvOrderMessage.setBackgroundColor(Color.WHITE);
        tvOrderDate.setBackgroundColor(Color.WHITE);
        tvOrderTotal.setBackgroundColor(Color.WHITE);

        if (order.isCancelled()) {
            tvOrderMessage.setText("Order Cancelled!");
            tvOrderDate.setText("Cancelled on " + Utils.formatDate(order.getCancelledOn(), "dd MMM yyyy hh:mm a"));
            tvOrderMessage.setTextColor(Color.RED);
        } else {
            tvOrderMessage.setText("Order Placed!");
            tvOrderDate.setText("Placed on " + Utils.formatDate(order.getTimestamp(), "dd MMM yyyy hh:mm a"));
        }

        float orderTotal = 0;
        ArrayList<CartItem> items = order.getItems();
        for ( CartItem item : items ) {
            orderTotal += item.getFoodItem().getPrice() * item.getQuantity();
        }
        tvOrderTotal.setText("₹ " + orderTotal);

        orderCard.setOnClickListener(v -> {
            Intent i = new Intent(activity, ViewOrder.class);
            i.putExtra("order", order);
            activity.startActivity(i);
        });

        String url = items.get(0).getFoodItem().getUrl();

        if (url != null) {
            Picasso.get().load(url).into(ivFoodImage);
        }

        return orderCard;
    }

    public static void setupCartMenu(Activity activity) {
        Button btnCart = activity.findViewById(R.id.btn_cart);
        btnCart.setOnClickListener(view -> Utils.startActivity(activity, CartActivity.class));
    }
        // To hide cancel button after certain minute
    public static long getDifferenceInMinutes(long timestamp1) {
        long timestamp2 = System.currentTimeMillis();
        long diffInMillis = Math.abs(timestamp2 - timestamp1); // Difference in milliseconds
        long diffInMinutes = diffInMillis / (60 * 1000); // Difference in minutes
        return diffInMinutes;
    }

    public static String formatDate(long millis, String format) {
        // Create a SimpleDateFormat object with the desired format
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // Convert the timestamp to a Date object
        Date date = new Date(millis);
        // Format the date as a string
        String dateString = sdf.format(date);
        // Return the formatted string
        return dateString;
    }
}
