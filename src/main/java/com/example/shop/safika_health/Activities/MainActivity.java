package com.example.shop.safika_health.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Model.Users;
import com.example.shop.safika_health.Model.healthProblem;
import com.example.shop.safika_health.MyNotificationPublisher;
import com.example.shop.safika_health.NewEncrypter;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button mjoinNowButton;
    private Button mloginButton;
    private ProgressDialog loadingBar;

    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mjoinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        mloginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });

        Paper.init(this);

        mjoinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });


        String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordkey);

        if (UserEmailKey != "" && UserPasswordKey != "") ;
        {
            if (!TextUtils.isEmpty(UserEmailKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                AllowAccess(UserEmailKey, UserPasswordKey);

                loadingBar.setTitle("Already Logged In");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }


        }
    }

    private void AllowAccess(final String email, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(email).exists()) {

                    Users usersData = dataSnapshot.child("Users").child(email).getValue(Users.class);

                    if (usersData.getEmail().equals(email)) {
                        if (usersData.getPassword().equals(password)) {


                            Prevalent.currentOnlineUser = usersData;

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

                            Prevalent.problems = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.child("healthProblem").getChildren()){
                                healthProblem problem = snapshot.getValue(healthProblem.class);
                                Prevalent.problems.add(problem);
                            }

                            Prevalent.all_products = new ArrayList<>();
                            Prevalent.recommended_products = new ArrayList<>();
                            Prevalent.favorited_products = new ArrayList<>();
                            for(DataSnapshot snapshot : dataSnapshot.child("product").getChildren()){
                                Products product = snapshot.getValue(Products.class);
                                Prevalent.all_products.add(product);
                                if (my_favorites.contains(product.getId())) {
                                    Prevalent.favorited_products.add(product);
                                }
                                List<String> array = Arrays.asList(product.getHealthProblem().split(","));
                                for (int i = 0 ;i<array.size();i++) {
                                    if (my_issues.contains(array.get(i))) {
                                        if (!Prevalent.recommended_products.contains(product)) {
                                            Prevalent.recommended_products.add(product);
                                        }

                                    }
                                }

                            }

                            scheduleNotification(getNotification( getResources().getString(R.string.app_name))) ;

                            Toast.makeText(MainActivity.this, "logged in Successfully..", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Account with this email does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    //  Toast.makeText(LoginActivity.this, "you need to create a new account" + email + "does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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

