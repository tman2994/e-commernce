package com.example.shop.safika_health.Activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.se.omapi.Session;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shop.safika_health.BuildConfig;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Model.Users;
import com.example.shop.safika_health.Model.healthProblem;
import com.example.shop.safika_health.MyNotificationPublisher;
import com.example.shop.safika_health.NewEncrypter;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.Provider.GMailSender;
import com.example.shop.safika_health.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

import io.paperdb.Paper;

import static com.example.shop.safika_health.Activities.RegisterActivity.md5;

public class LoginActivity extends AppCompatActivity {

    private EditText mInputEmail, mInputPassword;
    private Button mLoginButton;
    private TextView forgetPwd;
    private ProgressDialog loadingBar;

    private String parentDbName = "Users";
    private CheckBox chkRememberME;
    String forgot_email, verify_code;



    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// initialize activity
        setContentView(R.layout.activity_login);

        /// initialize items of page
        mInputEmail = (EditText) findViewById(R.id.login_email_input);
        mInputPassword = (EditText) findViewById(R.id.login_password_input);
        mLoginButton = (Button) findViewById(R.id.login_btn);
        forgetPwd = (TextView) findViewById(R.id.forgot_password_link);
        loadingBar = new ProgressDialog(this);
        chkRememberME = (CheckBox) findViewById(R.id.remember_me_chkb);

        /// action when click login button
        mLoginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                LoginUser();
            }

        });

        /// action when click forgot password
        forgetPwd.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                showEmailInputAlert();
            }

        });
    }

    private void LoginUser() {
        String password = mInputPassword.getText().toString();
        String email = mInputEmail.getText().toString();

        /// when email is null
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please write your email address..", Toast.LENGTH_SHORT).show();
        }
        /// when password is null
        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password..", Toast.LENGTH_SHORT).show();
        }else {
            /// login action
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(md5(email), md5(password));

        }
    }

    /// login function
    private void AllowAccessToAccount(final String email, final String password) {

        /// firebase initialize
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(email).exists()) {

                    Users usersData = dataSnapshot.child(parentDbName).child(email).getValue(Users.class);

                    /// if email and password is valid from databse
                    if (usersData.getEmail().equals(email)) {
                        if (usersData.getPassword().equals(password)) {

                            if(chkRememberME.isChecked()) {
                                Paper.book().write(Prevalent.UserEmailKey, email);
                                Paper.book().write(Prevalent.UserPasswordkey, password);
                            }


                            Prevalent.currentOnlineUser = usersData;

                            /// make user's health issue array
                            List<String> my_issues = new ArrayList<>();
                            for(String issue: usersData.getHealthProblems()) {
                                try {
                                    String decrypted = NewEncrypter.decrypt(issue);
                                    my_issues.add(decrypted);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Prevalent.currentOnlineUser.setHealthProblems(my_issues);
                            List<String> my_favorites = usersData.getFavorited();

                            /// get all category from database
                            Prevalent.problems = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.child("healthProblem").getChildren()){
                                healthProblem problem = snapshot.getValue(healthProblem.class);
                                Prevalent.problems.add(problem);
                            }

                            /// initialize products, favo products, recommended products
                            Prevalent.all_products = new ArrayList<>();
                            Prevalent.recommended_products = new ArrayList<>();
                            Prevalent.recommended_products = new ArrayList<>();

                            /// get all , favo, recommended products from database
                            for(DataSnapshot snapshot : dataSnapshot.child("product").getChildren()){
                                Products product = snapshot.getValue(Products.class);
                                Prevalent.all_products.add(product);
                                List<String> array = Arrays.asList(product.getHealthProblem().split(","));
                                for (int i = 0 ;i<array.size();i++) {
                                    if (my_issues.contains(array.get(i))) {
                                        if (my_issues.contains(array.get(i))) {
                                            if (!Prevalent.recommended_products.contains(product)) {
                                                Prevalent.recommended_products.add(product);
                                            }

                                        }
                                        break;
                                    }
                                }
                                if (my_favorites.contains(product.getId())) {
                                    Prevalent.favorited_products.add(product);
                                }
                            }

                            Toast.makeText(LoginActivity.this, "logged in Successfully..", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            scheduleNotification(getNotification( getResources().getString(R.string.app_name))) ;

                            /// go to main activity
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Account with this email does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void showEmailInputAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input user email");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Send Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progress_dialog = new ProgressDialog(LoginActivity.this);
                progress_dialog.setTitle("Checking user eamil");
                progress_dialog.setMessage("Please wait");
                progress_dialog.show();
                forgot_email = md5(String.valueOf(input.getText()));
                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference();

                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(parentDbName).child(forgot_email).exists()) {

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
                                                String.valueOf(input.getText()));
                                        progress_dialog.dismiss();

                                        LoginActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showEmailVerifyAlert();
                                            }
                                        });

//
                                    } catch (Exception e) {
                                        Log.e("mylog", "Error: " + e.getMessage());
                                    }
                                }
                            });
                            sender.start();

                        } else {
                            Toast.makeText(LoginActivity.this, "Account with this email does not exist", Toast.LENGTH_SHORT).show();
                            progress_dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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

    void showEmailVerifyAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input verify code");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String code = String.valueOf(input.getText());
                if (verify_code.equals(code)) {
                    showNewPasswordAlert();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Failed to verify email", Toast.LENGTH_SHORT).show();
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

    void showNewPasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set new password");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog progress_dialog = new ProgressDialog(LoginActivity.this);
                progress_dialog.setTitle("Updating user password");
                progress_dialog.setMessage("Please wait");
                progress_dialog.show();
                final String new_pwd = md5(String.valueOf(input.getText()));

                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference().child("Users");

                RootRef.orderByChild("email").equalTo(forgot_email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists())) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                snapshot.getRef().child("password").setValue(new_pwd);
                            }
                            progress_dialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Success to update password", Toast.LENGTH_SHORT).show();

                        } else {
                            progress_dialog.dismiss();
                            Toast.makeText(LoginActivity.this,"This account is not exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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

    public void scheduleNotification (Notification notification) {
        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , notification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 100 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    public Notification getNotification (String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle("You have " + String.valueOf(Prevalent.recommended_products.size()) + " recommended productions") ;
        builder.setContentText(content) ;
        builder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }



}