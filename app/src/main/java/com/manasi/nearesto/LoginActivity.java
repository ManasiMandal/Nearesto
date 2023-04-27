package com.manasi.nearesto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.helper.Crypto;
import com.manasi.nearesto.helper.Utils;
import com.manasi.nearesto.modal.User;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView linkRegister;
    private ProgressDialog pd;

    private FirebaseFirestore db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin     = findViewById(R.id.btn_login);
        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        linkRegister = findViewById(R.id.link_register);

        btnLogin.setOnClickListener(view -> login());
        linkRegister.setOnClickListener(view -> Utils.startActivity(this, SignUpActivity.class));

        String recentUserEmail = getIntent().getStringExtra("email");
        if(recentUserEmail != null) {
            etEmail.setText(recentUserEmail);
            etPassword.getText().clear();
        }

        db = FirebaseFirestore.getInstance();
    }

    private void login() {

        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if ( email.length() == 0 ) {
            Toast.makeText(this, "Email required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( password.length() == 0 ) {
            Toast.makeText(this, "Password required!", Toast.LENGTH_SHORT).show();
            return;
        }

        pd = Utils.progressDialog(this, "Please wait...");
        pd.show();

        db.collection("users")
                .document(Utils.getID(email))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {

                        if(task.getResult().exists() && Crypto.decrypt(task.getResult().get("password", String.class)).equals(password)) {
                            User user = task.getResult().toObject(User.class);
                            Utils.addUserToSharedPreferences(this, user);
                            Utils.addRecentUserToSharedPreferences(this, user);
                            startActivity(new Intent(this, HomeActivity.class));
                        }
                        else {
                            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                })
                .addOnFailureListener(ex -> {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
