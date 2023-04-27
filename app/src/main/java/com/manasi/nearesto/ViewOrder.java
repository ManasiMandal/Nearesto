package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.CartItem;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Order;

import java.util.ArrayList;

public class ViewOrder extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout foodItemsContainer;
    private TextView tvMessage, tvOrderTotal;
    private Button btnCancelOrder, btnViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> finish() );

        foodItemsContainer = findViewById(R.id.food_items_container);
        tvMessage = findViewById(R.id.tv_message);
        tvOrderTotal = findViewById(R.id.tv_order_total);
        Order order = (Order) getIntent().getSerializableExtra("order");
        Boolean orderPlaced = getIntent().getBooleanExtra("order_placed", false);

        if (! orderPlaced) {
            if ( order.getCancelledOn() > 0 ) {
                tvMessage.setText("This order was cancelled on " + Utils.formatDate(order.getCancelledOn(), "dd MMM yyyy hh:mm a"));
                tvMessage.setTextColor(Color.RED);
            } else {
                tvMessage.setText("This order was placed on " + Utils.formatDate(order.getTimestamp(), "dd MMM yyyy hh:mm a"));
            }
        }

        db = FirebaseFirestore.getInstance();

        loadFoodItems(order.getItems());

        btnCancelOrder = findViewById(R.id.btn_cancel_order);

        if ( Utils.getDifferenceInMinutes(order.getTimestamp()) > 30 || order.isCancelled() ) {
            btnCancelOrder.setEnabled(false);
            btnCancelOrder.setVisibility(View.GONE);
        }

        btnCancelOrder.setOnClickListener(view -> {

            ProgressDialog loader = Utils.progressDialog(this, "Please wait!");
            loader.show();

            order.setStatus("cancelled");
            order.setCancelledOn(System.currentTimeMillis());

            db.collection("orders")
                    .document("" + order.getTimestamp())
                    .set(order)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Utils.toast(this, "Order Cancelled!");
                            finish();
                            startActivity(getIntent());
                        } else {
                            Utils.toast(this, task.getException().getMessage());
                        }
                        loader.dismiss();
                    })
                    .addOnFailureListener(ex -> {
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        loader.dismiss();
                    });
        });

        btnViewOrders = findViewById(R.id.btn_view_orders);
        btnViewOrders.setOnClickListener(view -> Utils.startActivity(this, OrdersActivity.class));
    }

    private void loadFoodItems(ArrayList<CartItem> cartItems) {
        foodItemsContainer.removeAllViews();
        float orderTotal = 0;
        for ( CartItem cartItem : cartItems ) {
            FoodItem foodItem = cartItem.getFoodItem();
            RelativeLayout foodItemCard = Utils.prepareFoodItemCard(this, foodItem);
            TextView tvRestaurantName = foodItemCard.findViewById(R.id.tv_restaurant_name);
            tvRestaurantName.setText("Quantity: " + cartItem.getQuantity());
            foodItemsContainer.addView(foodItemCard);
            orderTotal += foodItem.getPrice() * cartItem.getQuantity();
        }
        tvOrderTotal.setText("₹ " + orderTotal);
    }
}