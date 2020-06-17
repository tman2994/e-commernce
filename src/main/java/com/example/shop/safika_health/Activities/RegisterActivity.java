package com.example.shop.safika_health.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Model.Users;
import com.example.shop.safika_health.Model.healthProblem;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.Provider.GMailSender;
import com.example.shop.safika_health.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private Button mCreateAccountButton;
    private EditText mInputName, mInputEmailAddress, mInputPassword;
    private ProgressDialog loadingBar;
    Handler mHandler;
    String name, email, password, verify_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mHandler=new Handler();

        mCreateAccountButton = (Button) findViewById(R.id.register_btn);
        mInputName = (EditText) findViewById(R.id.register_username_input);
        mInputEmailAddress = (EditText) findViewById(R.id.register_Email_input);
        mInputPassword = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();

            }

        });
    }
    private void CreateAccount() {

            name = mInputName.getText().toString();
            email= mInputEmailAddress.getText().toString();
            password = mInputPassword.getText().toString();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please write your name..", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please write your email address..", Toast.LENGTH_SHORT).show();
            } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please write your password..", Toast.LENGTH_SHORT).show();
            } else {
                final ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                dialog.setTitle("Sending verify code");
                dialog.setMessage("Please wait");
                dialog.show();

                Thread sender = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Random rnd = new Random();
                            int number = rnd.nextInt(999999);

                            // this will convert any number sequence into 6 character.
                            verify_code =  String.format("%06d", number);
                            GMailSender sender = new GMailSender(getResources().getString(R.string.main_email_id), getResources().getString(R.string.main_email_pwd));
                            sender.sendMail("Safika Health",
                                    "This is code : " + verify_code,
                                    "support@safika_health.com",
                                    mInputEmailAddress.getText().toString());
                            dialog.dismiss();

                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmailVeirfyAlert();
                                }
                            });

//
                        } catch (Exception e) {
                            Log.e("mylog", "Error: " + e.getMessage());
                        }
                    }
                });
                sender.start();
            }
    }

    void showEmailVeirfyAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input verify code");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = input.getText().toString();
                if (verify_code.equals(code)) {
                    loadingBar.setTitle("Create Account");
                    loadingBar.setMessage("Please wait, while we are checking the credentials");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    ValidateEmail(name, md5(email), md5(password));
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Failed to verify email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    };

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void ValidateEmail(final String name, final String email, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("Users").child(email).exists())) {

                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => "+c.getTime());

                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    String user_id = "user_" + format.format(c.getTime());

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("id", user_id);
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(email).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Your account was created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Prevalent.problems = new ArrayList<>();
                                        for(DataSnapshot snapshot : dataSnapshot.child("healthProblem").getChildren()){
                                            healthProblem problem = snapshot.getValue(healthProblem.class);
                                            Prevalent.problems.add(problem);
                                        }

                                        Prevalent.all_products = new ArrayList<>();
                                        Prevalent.recommended_products = new ArrayList<>();

                                        for(DataSnapshot snapshot : dataSnapshot.child("product").getChildren()){

                                            Prevalent.all_products.add(snapshot.getValue(Products.class));
                                            Prevalent.recommended_products.add(snapshot.getValue(Products.class));
                                        }

                                        Prevalent.currentOnlineUser = dataSnapshot.child("User").child(email).getValue(Users.class);

                                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(RegisterActivity.this,"This email already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please try again with a different email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


