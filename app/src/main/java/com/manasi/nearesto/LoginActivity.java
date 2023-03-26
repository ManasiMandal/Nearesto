package com.manasi.nearesto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.manasi.nearesto.R;

//import Model.BaseFirestore;
//import Model.Customer;
//import Model.Product;
//import Model.PromoCodes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity
{
    TextView email;
    TextView password;
    Button signin;
    TextView signup;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences=context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        email=findViewById(R.id.edittext_login_email);
        password=findViewById(R.id.editText_password);
        signup=findViewById(R.id.text_signup);

        System.out.println("sharedPreferences" + sharedPreferences.getString("customerType","null"));
        email.setText(sharedPreferences.getString("email",""));
        password.setText(sharedPreferences.getString("password",""));

        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        System.out.println("INSIDE LOGIN CLASS -----------------------");
        System.out.println("Initially : "+signin);
        signin=findViewById(R.id.button_signin);
        System.out.println("Finally : "+signin);
//        signin.setOnClickListener(new View.OnClickListener()
//        {
//            //Toast.makeText(getApplicationContext(),"User exists with password :",Toast.LENGTH_SHORT).show();
//            @Override
//            public void onClick(View view)
//            {
//                final String e_mail = email.getText().toString();
//                DocumentReference docref = Customer.db.collection("Customer").document(e_mail);
//                docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//                {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
//                    {
//
//                        if(task.isSuccessful())
//                        {
//                            DocumentSnapshot doc = task.getResult();
//                            if(doc.exists())
//                            {
//                                String pa = doc.getString("password");
//                                Customer c = doc.toObject(Customer.class);
//                                //System.out.println(c);
//                                if(pa.equals(password.getText().toString())) {
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    editor.putString("email",email.getText().toString());
//                                    editor.putString("password",pa);
//                                    editor.putString("customerType", "Customer");
//                                    editor.commit();
//                                    go_to_Customet_Home();
//                                }
//                                else {
//                                    Toast.makeText(getApplicationContext(),
//                                            "Incorrect Password customer",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                            else
//                            {
//                                final DocumentReference  doc2= BaseFirestore.db.collection("DeliveryPartner").document(e_mail);
//                                doc2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
//                                {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
//                                    {
//                                        if(task.isSuccessful())
//                                        {
//                                            DocumentSnapshot ref = task.getResult();
//                                            if(ref.exists())
//                                            {
//                                                String pa = ref.getString("password");
//                                                if(pa.equals(password.getText().toString()))
//                                                {
//                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                                    editor.putString("email",email.getText().toString());
//                                                    editor.putString("password",pa);
//                                                    editor.commit();
//                                                }
//                                                else
//                                                {
//                                                    Toast.makeText(getApplicationContext(),"Incorrect Password delivery",Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                            else
//                                            {
//                                                Toast.makeText(getApplicationContext(),"User does not exist , please sign up",Toast.LENGTH_SHORT).show();
////                                                finish();
//                                            }
//                                        }
//                                        else
//                                        {
//
//                                        }
//                                    }
//                                });
//                                //Toast.makeText(getApplicationContext(),"User does not exist , please sign up",Toast.LENGTH_SHORT).show();
//                                //finish();
//                                //go to previous activity
//                            }
//                        }
//                        else
//                        {
////                            //now check DileveryPartner collection.
////                            Toast.makeText(getApplicationContext(),"Error connecting to servers",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
    }

}
