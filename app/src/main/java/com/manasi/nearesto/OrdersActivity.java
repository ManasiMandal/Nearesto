package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Order;
import com.manasi.nearesto.modal.User;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private LinearLayout noOrders, ordersContainer;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        new MenuNavigation(this);
        Utils.setupCartMenu(this);

        db = FirebaseFirestore.getInstance();
        ordersContainer = findViewById(R.id.orders_container);
        noOrders = findViewById(R.id.no_orders);
        user = Utils.getUserFromSharedPreferences(this);

        noOrders.setVisibility(View.GONE);

        loadOrders();
    }

    private void loadOrders() {
        ProgressDialog loader = Utils.progressDialog(this, "Loading order items...");
        loader.show();

        db.collection("orders")
                .whereEqualTo("user", user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<Order> orders = task.getResult().toObjects(Order.class);
                        setOrdersInContainer(orders);
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("ERR MESSAGE", task.getException().getMessage());
                    }
                    loader.dismiss();
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ERR MESSAGE", ex.getMessage());
                    loader.dismiss();
                });
    }

    private void setOrdersInContainer(List<Order> orders) {
        ordersContainer.removeAllViews();
        noOrders.setVisibility(View.VISIBLE);
        for (Order order: orders) {
            RelativeLayout orderCard = Utils.prepareOrderCard(this, order);
            ordersContainer.addView(orderCard);
            noOrders.setVisibility(View.GONE);
        }
    }
}