package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.CartItem;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Order;
import com.manasi.nearesto.modal.Restaurant;
import com.manasi.nearesto.modal.User;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout cartItemsContainer, noCartItems;
    private TextView tvCartTotal;
    private Button btnPlaceOrder;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        new MenuNavigation(this);

        db = FirebaseFirestore.getInstance();

        cartItemsContainer = findViewById(R.id.cart_items_container);
        noCartItems        = findViewById(R.id.no_cart_items);
        tvCartTotal        = findViewById(R.id.tv_cart_total);
        btnPlaceOrder      = findViewById(R.id.btn_place_order);


        user = Utils.getUserFromSharedPreferences(this);
        if ( user.getEmail() == null ) {
            btnPlaceOrder.setText("Login to Place Order");
        }

        btnPlaceOrder.setOnClickListener(view -> {
            if ( user.getEmail() == null ) {
                Utils.startActivity(this, LoginActivity.class);
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Nearesto")
                    .setIcon(R.drawable.ic_nearesto)
                    .setMessage("Confirm Order?")
                    .setPositiveButton("Yes", (dialog, which) -> placeOrder())
                    .setNegativeButton("NO", null)
                    .show();
        });

        loadCartItems();
    }

    private void placeOrder() {
        ArrayList<CartItem> cartItems = Utils.getCartItems(this);
        if ( cartItems.size() == 0 ) {
            Utils.toast(this, "No items added in the cart!");
            return;
        }

        ProgressDialog loader = Utils.progressDialog(this, "Placing order...");
        loader.show();

        Order order = new Order(cartItems, user.getEmail());
        db.collection("orders")
                .document("" + order.getTimestamp())
                .set(order)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Utils.toast(this, "Order placed");
                        Utils.clearCart(this);
                        Intent intent = new Intent(CartActivity.this, ViewOrder.class);
                        intent.putExtra( "order", order );
                        intent.putExtra("order_placed", true);
                        startActivity(intent);
                    } else {
                        Utils.toast(this, task.getException().getMessage());
                    }
                    loader.dismiss();
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                });
    }

    private void loadCartItems() {
        ProgressDialog loader = Utils.progressDialog(this, "Loading items...");
        loader.show();
        cartItemsContainer.removeAllViews();
        float cartTotal = 0;
        ArrayList<CartItem> cartItems = Utils.getCartItems(this);
        for(CartItem cartItem : cartItems) {
            FoodItem foodItem = cartItem.getFoodItem();
            int quantity      = cartItem.getQuantity();

            RelativeLayout cartItemCard = (RelativeLayout) this.getLayoutInflater().inflate(R.layout.cart_item_card, null);

            ImageView ivFoodImage     = cartItemCard.findViewById(R.id.iv_image);
            TextView tvName           = cartItemCard.findViewById(R.id.tv_name);
            TextView tvRestaurantName = cartItemCard.findViewById(R.id.tv_restaurant_name);
            TextView tvPrice          = cartItemCard.findViewById(R.id.tv_price);
            ImageView ivType          = cartItemCard.findViewById(R.id.iv_type);
            EditText etQuantity       = cartItemCard.findViewById(R.id.et_quantity);
            Button btnDecrease        = cartItemCard.findViewById(R.id.btn_decrease);
            Button btnIncrease        = cartItemCard.findViewById(R.id.btn_increase);
            Button btnRemove          = cartItemCard.findViewById(R.id.btn_remove);

            tvName.setText(foodItem.getName());
            tvRestaurantName.setText(foodItem.getRestaurantName());
            tvPrice.setText("₹ " + (quantity*foodItem.getPrice()));

            cartTotal += quantity*foodItem.getPrice();

            if (foodItem.isNonVeg()) {
                ivType.setBackgroundResource(R.drawable.non_veg);
            } else {
                ivType.setBackgroundResource(R.drawable.veg);
            }

            String url = foodItem.getUrl();
            if (url != null) {
                Picasso.get().load(url).into(ivFoodImage);
            }

            etQuantity.setText("" + quantity);

            btnDecrease.setOnClickListener(view -> {
                int q = Integer.parseInt("0" + etQuantity.getText().toString());
                if( q > 1 ) {
                    cartItem.setQuantity(--q);
                    Utils.addItemToCart(this, cartItem, true);
                    etQuantity.setText("" + q);
                    tvPrice.setText("₹ " + (q*foodItem.getPrice()));

                    float total = Float.parseFloat(tvCartTotal.getText().toString().replace("₹", ""));
                    tvCartTotal.setText("₹ " + (total - foodItem.getPrice()));
                }
            });

            btnIncrease.setOnClickListener(view -> {
                int q = Integer.parseInt("0" + etQuantity.getText().toString());
                if( q < 100 ) {
                    cartItem.setQuantity(++q);
                    Utils.addItemToCart(this, cartItem, true);
                    etQuantity.setText("" + q);
                    tvPrice.setText("₹ " + (q*foodItem.getPrice()));

                    float total = Float.parseFloat(tvCartTotal.getText().toString().replace("₹", ""));
                    tvCartTotal.setText("₹ " + (total + foodItem.getPrice()));
                }
            });

            btnRemove.setOnClickListener(view -> {
                Utils.removeItemFromCart(this, cartItem);
                cartItemsContainer.removeView(cartItemCard);
                if(cartItemsContainer.getChildCount() == 0) {
                    noCartItems.setVisibility(View.VISIBLE);
                }

                float total = Float.parseFloat(tvCartTotal.getText().toString().replace("₹", ""));
                tvCartTotal.setText("₹ " + (total - (cartItem.getQuantity() * cartItem.getFoodItem().getPrice())));
            });

            ivFoodImage.setOnClickListener(v -> {
                Intent i = new Intent(this, ViewFoodItem.class);
                i.putExtra("restaurant_id", foodItem.getRestaurant());
                i.putExtra("food_item", foodItem);
                this.startActivity(i);
            });

            cartItemsContainer.addView(cartItemCard);
            noCartItems.setVisibility(View.GONE);
        }
        tvCartTotal.setText("₹ " + cartTotal);
        loader.dismiss();
    }
}