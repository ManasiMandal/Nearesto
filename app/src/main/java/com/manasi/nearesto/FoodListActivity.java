package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout foodItemsContainer;
    private Spinner spFilterBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        new MenuNavigation(this);
        Utils.setupCartMenu(this);

        foodItemsContainer = findViewById(R.id.food_items_container);
        spFilterBy         = findViewById(R.id.sp_filter_by);

        spFilterBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Code to execute when an item is selected
                String selectedItem = parent.getItemAtPosition(position).toString();
                selectedItem = selectedItem.equals("Filter By") ? "" : selectedItem.replace("-", "").toLowerCase();
                loadFoodItems(selectedItem);
//                Toast.makeText(FoodListActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Code to execute when nothing is selected
                Toast.makeText(FoodListActivity.this, "Nothing selected!", Toast.LENGTH_SHORT).show();
            }
        });

        db = FirebaseFirestore.getInstance();
    }

    private void loadFoodItems(String keyword) {

        ProgressDialog loader = Utils.progressDialog(this, "Loading items...");
        loader.show();

        db.collection("items")
                .orderBy("restaurant", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<FoodItem> foodItems = task.getResult().toObjects(FoodItem.class);
                        List<FoodItem> filteredItems = foodItems;
                        if (! keyword.isEmpty()) {
                            filteredItems = new ArrayList<>();
                            for (FoodItem foodItem : foodItems) {
                                if (foodItem.getKeywords().contains("|" + keyword + "|")) {
                                    filteredItems.add(foodItem);
                                }
                            }
                        }
                        setFoodItemsInContainer(filteredItems);
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

    private void setFoodItemsInContainer(List<FoodItem> foodItems) {
        foodItemsContainer.removeAllViews();
        for (FoodItem foodItem: foodItems) {
            RelativeLayout foodItemCard = Utils.prepareFoodItemCard(this, foodItem);
            foodItemsContainer.addView(foodItemCard);
        }
    }

}