package com.manasi.nearesto.helper;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.R;
import com.manasi.nearesto.modal.FoodItem;
import com.manasi.nearesto.modal.Restaurant;

public class CSVDataImporter {

    public static void importRestaurants(Activity context) throws IOException {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String filePath = "android.resource://" + context.getPackageName() + "/" + R.raw.nearesto_restaurants;

        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(filePath));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String[] headers;
        HashMap<String, Integer> index = new HashMap<>();

        int count = -1;
        String line;
        while ((line = br.readLine()) != null) {

            if (count == -1) {
                headers = line.split(",");
                index.put("id", getColumnIndex(headers, "id"));
                index.put("name", getColumnIndex(headers, "name"));
                index.put("description", getColumnIndex(headers, "description"));
                index.put("type", getColumnIndex(headers, "type"));
                index.put("rating", getColumnIndex(headers, "rating"));
                index.put("address1", getColumnIndex(headers, "address1"));
                index.put("address2", getColumnIndex(headers, "address2"));
                index.put("pincode", getColumnIndex(headers, "pincode"));
                index.put("city", getColumnIndex(headers, "city"));
                index.put("state", getColumnIndex(headers, "state"));
                index.put("latitude", getColumnIndex(headers, "latitude"));
                index.put("longitude", getColumnIndex(headers, "longitude"));
                index.put("url", getColumnIndex(headers, "url"));
                count++;
                continue;
            }

            String[] res = line.split(",");

            Restaurant restaurant = new Restaurant();
            restaurant.setId(Long.valueOf(res[index.get("id")]));
            restaurant.setName(res[index.get("name")].replace("###", ","));
            restaurant.setDescription(res[index.get("description")].replace("###", ","));
            restaurant.setType(Integer.parseInt(res[index.get("type")]));
            restaurant.setRating(Float.valueOf(res[index.get("rating")]));
            restaurant.setAddress1(res[index.get("address1")].replace("###", ","));
            restaurant.setAddress2(res[index.get("address2")].replace("###", ","));
            restaurant.setPincode(res[index.get("pincode")]);
            restaurant.setCity(res[index.get("city")].replace("###", ","));
            restaurant.setState(res[index.get("state")].replace("###", ","));
            restaurant.setLatitude(Float.parseFloat(res[index.get("latitude")]));
            restaurant.setLongitude(Float.parseFloat(res[index.get("longitude")]));
            if ( res.length == 13 ) {
                restaurant.setUrl(res[index.get("url")]);
            }

            db.collection("restaurants")
                    .document(res[index.get("id")])
                    .set(restaurant)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
//                            Toast.makeText( context, "Uploaded...", Toast.LENGTH_SHORT ).show();
                        } else {
                            Toast.makeText( context, task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    })
                    .addOnFailureListener(ex -> {
                        Toast.makeText( context, ex.getMessage(), Toast.LENGTH_SHORT ).show();
                    });
            count++;
        }
        Toast.makeText(context, count + " restaurants updated!", Toast.LENGTH_SHORT).show();

        br.close();
    }

    public static void importFoodItems(Activity context) throws IOException {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String filePath = "android.resource://" + context.getPackageName() + "/" + R.raw.nearesto_food_items;

        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(filePath));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String[] headers;
        HashMap<String, Integer> index = new HashMap<>();

        int count = -1;
        String line;
        while ((line = br.readLine()) != null) {

            if (count == -1) {
                headers = line.split(",");
                index.put("id",getColumnIndex(headers, "id"));
                index.put("name",getColumnIndex(headers, "name"));
                index.put("description",getColumnIndex(headers, "description"));
                index.put("price",getColumnIndex(headers, "price"));
                index.put("type",getColumnIndex(headers, "type"));
                index.put("rating",getColumnIndex(headers, "rating"));
                index.put("restaurant",getColumnIndex(headers, "restaurant"));
                index.put("restaurantName",getColumnIndex(headers, "restaurantName"));
                index.put("keywords",getColumnIndex(headers, "keywords"));
                index.put("url",getColumnIndex(headers, "url"));
                count++;
                continue;
            }

            String[] item = line.split(",");

            FoodItem foodItem = new FoodItem();
            foodItem.setId(Long.valueOf(item[index.get("id")]));
            foodItem.setName(item[index.get("name")]);
            foodItem.setDescription(item[index.get("description")].replace("###", ","));
            foodItem.setPrice(Float.valueOf(item[index.get("price")]));
            foodItem.setType(item[index.get("type")]);
            foodItem.setRating(Float.valueOf(item[index.get("rating")]));
            foodItem.setRestaurant(Long.valueOf("0" + item[index.get("restaurant")]));
            foodItem.setRestaurantName(item[index.get("restaurantName")]);
            foodItem.setKeywords(item[index.get("keywords")]);
            if ( item.length == 10 ) {
                foodItem.setUrl(item[index.get("url")]);
            }

            db.collection("items")
                    .document(item[index.get("id")])
                    .set(foodItem)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
//                            Toast.makeText( context, "Uploaded...", Toast.LENGTH_SHORT ).show();
                        } else {
                            Toast.makeText( context, task.getException().getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    })
                    .addOnFailureListener(ex -> {
                        Toast.makeText( context, ex.getMessage(), Toast.LENGTH_SHORT ).show();
                    });
            count++;
        }
        Toast.makeText(context, count + " items updated!", Toast.LENGTH_SHORT).show();

        br.close();
    }

    private static int getColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
