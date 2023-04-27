package com.manasi.nearesto;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.MenuNavigation;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.User;

public class MyProfileActivity extends AppCompatActivity {

    private ImageButton btnLogout;
    private Button btnUpdate;
    private EditText etName, etEmail, etPhone;
    private String updatedName, updatedPhone;
    private ProgressDialog pd;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        new MenuNavigation(this);

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setIcon(R.drawable.ic_nearesto)
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES", (dialog, which) -> {
                        Utils.clearLoginPreferences(this);
                        Utils.startActivity(this, HomeActivity.class);
                    })
                    .setNegativeButton("NO", null)
                    .show();
        });

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);

        btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(view -> updateProfile());

        user = Utils.getUserFromSharedPreferences(this);

        if ( user.getEmail() != null ) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
        } else {
            Utils.startActivity(this, LoginActivity.class);
        }
    }

    private boolean validateData(String name, String phone) {
        if(name.isEmpty()) {
            etName.setError("Name is required!");
            etName.requestFocus();
            return false;
        }

        if(phone.isEmpty()) {
            etPhone.setError("Phone number is required!");
            etPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void updateProfile() {
        updatedName  = etName.getText().toString().trim();
        updatedPhone = etPhone.getText().toString().trim();

        if(validateData(updatedName, updatedPhone) &&
                (!updatedName.equals(user.getName()) || !updatedPhone.equals(user.getPhone()))) {

            pd = Utils.progressDialog(this, "Updating profile...");
            pd.show();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(Utils.getID(user.getEmail()))
                    .get()
                    .addOnSuccessListener(document -> {
                        User user = document.toObject(User.class);
                        user.setName(updatedName);
                        user.setPhone(updatedPhone);
                        updateUserDataToFirestore(user);
                    })
                    .addOnFailureListener(ex -> {
                        pd.dismiss();
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserDataToFirestore(User user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(Utils.getID(user.getEmail()))
                .set(user)
                .addOnSuccessListener(unused -> {
                    Utils.toast(this, "Profile Updated...");
                    updateSharedPreferences(user);
                    pd.dismiss();
                })
                .addOnFailureListener(ex -> {
                    pd.dismiss();
                    Utils.toast(this, ex.getMessage());
                });
    }

    private void updateSharedPreferences(User user) {
        SharedPreferences.Editor editor = getSharedPreferences(Utils.LOGIN_SHARED_FILE, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.putString("phone", user.getPhone());
        editor.apply();

        editor = getSharedPreferences(Utils.RECENT_USER_SHARED_FILE, MODE_PRIVATE).edit();
        editor.putString("name", user.getName());
        editor.apply();
    }
}