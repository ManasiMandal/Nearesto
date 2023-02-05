package com.manasi.nearesto.helper;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.manasi.nearesto.HomeActivity;
import com.manasi.nearesto.MyProfileActivity;
import com.manasi.nearesto.OrdersActivity;
import com.manasi.nearesto.R;
import com.manasi.nearesto.RestaurantsActivity;
import com.manasi.nearesto.SearchActivity;

public class MenuNavigation {
    Button btnHome, btnSearch, btnOrders, btnMyProfile, btnResturants;
    public MenuNavigation(Activity activity) {
        btnHome = activity.findViewById(R.id.btn_home);
        btnSearch = activity.findViewById(R.id.btn_search);
        btnOrders = activity.findViewById(R.id.btn_orders);
        btnMyProfile = activity.findViewById(R.id.btn_my_profile);
        btnResturants = activity.findViewById(R.id.btn_restaurants);

        btnHome.setOnClickListener(view -> activity.startActivity(new Intent(activity, HomeActivity.class)));
        btnSearch.setOnClickListener(view -> activity.startActivity(new Intent(activity, SearchActivity.class)));
        btnOrders.setOnClickListener(view -> activity.startActivity(new Intent(activity, OrdersActivity.class)));
        btnMyProfile.setOnClickListener(view -> activity.startActivity(new Intent(activity, MyProfileActivity.class)));
        btnResturants.setOnClickListener(view -> activity.startActivity(new Intent(activity, RestaurantsActivity.class)));

        if (activity.getClass().equals(HomeActivity.class)) {
            btnHome.setOnClickListener(null);
        }
        if (activity.getClass().equals(SearchActivity.class)) {
            btnSearch.setOnClickListener(null);
        }
        if (activity.getClass().equals(OrdersActivity.class)) {
            btnOrders.setOnClickListener(null);
        }
        if (activity.getClass().equals(MyProfileActivity.class)) {
            btnMyProfile.setOnClickListener(null);
        }
        if (activity.getClass().equals(RestaurantsActivity.class)) {
            btnResturants.setOnClickListener(null);
        }

    }
}
