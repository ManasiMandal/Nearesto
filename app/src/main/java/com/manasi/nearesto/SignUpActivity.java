package com.manasi.nearesto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.Crypto;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.User;

public class SignUpActivity extends AppCompatActivity
{
    private EditText etName, etEmail, etPassword, etPhone;
    private Button btnSignUp;
    private TextView linkLogin;
    private ProgressDialog pd;

    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName     = findViewById(R.id.et_name);
        etEmail    = findViewById(R.id.et_email);
        etPhone    = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnSignUp  = findViewById(R.id.btn_signup);
        linkLogin  = findViewById(R.id.link_login);

        btnSignUp.setOnClickListener(view -> {

            String name     = etName.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String phone    = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(validateData(name, email, phone, password)) {
                User user = new User(name, email, Crypto.encrypt(password));
                user.setPhone(phone);
                newRegistration(user);
            }
        });

        linkLogin.setOnClickListener(view -> Utils.startActivity(this, LoginActivity.class));

        db = FirebaseFirestore.getInstance();
    }
    private boolean validateData(String name, String email, String phone, String password) {
        if(name.isEmpty()) {
            etName.setError("Name is required!");
            etName.requestFocus();
            return false;
        }

        if(email.isEmpty()) {
            etEmail.setError("Email is required!");
            etEmail.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please provide valid email!");
            etEmail.requestFocus();
            return false;
        }

        if(phone.isEmpty()) {
            etPhone.setError("City is required!");
            etPhone.requestFocus();
            return false;
        }

        if(password.isEmpty()) {
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return false;
        }

        if(password.length() < 6) {
            etPassword.setError("Minimum password length should be 6 characters!");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void newRegistration(User user) {

        pd = Utils.progressDialog(this, "Please wait...");
        pd.show();

        db.collection("users")
                .document(Utils.getID(user.getEmail()))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        if(!task.getResult().exists()) {
                            registerUser(user);
                        } else {
                            etEmail.setError("Email already registered!");
                            etEmail.requestFocus();
                        }
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                });
    }

    private void registerUser(User user) {

        db.collection("users")
                .document(Utils.getID(user.getEmail()))
                .set(user)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "User has been registered successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.putExtra("email", user.getEmail());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                });
    }

}
