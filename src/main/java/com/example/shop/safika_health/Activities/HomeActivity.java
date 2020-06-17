package com.example.shop.safika_health.Activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shop.safika_health.Fragments.InformationFragment;
import com.example.shop.safika_health.Fragments.ProductsFragment;
import com.example.shop.safika_health.Fragments.ProfileFragment;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
        Picasso.get()
                .load(Prevalent.currentOnlineUser.getPhoto())
                .placeholder(R.drawable.profile)
                .into(profileImageView);

        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        getSupportActionBar().setTitle("Product");

        getSupportFragmentManager().beginTransaction().replace(R.id.myframe, ProductsFragment.getInstance()).addToBackStack(null).commit();

    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        getSupportActionBar().setTitle(item.getTitle());

        Fragment selectedFragment = null;
        switch (id){
            case R.id.nav_info:
                selectedFragment = InformationFragment.getInstance();
                break;
            case R.id.nav_product:
                Prevalent.product_page_index = 0;
                selectedFragment = ProductsFragment.getInstance();
                break;
            case R.id.nav_recommend:
                Prevalent.product_page_index = 1;
                selectedFragment = ProductsFragment.getInstance();
                break;
            case R.id.nav_favorites:
                Prevalent.product_page_index = 2;
                selectedFragment = ProductsFragment.getInstance();
                break;
            case R.id.nav_cart:
                Prevalent.product_page_index = 3;
                selectedFragment = ProductsFragment.getInstance();
                break;
            case R.id.nav_profile:
                selectedFragment = ProfileFragment.getInstance();
                break;
            case R.id.nav_logout:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Prevalent.currentOnlineUser = null;
                Prevalent.all_products = new ArrayList<Products>();
                Prevalent.recommended_products = new ArrayList<Products>();
                Prevalent.favorited_products = new ArrayList<Products>();
                Prevalent.product_page_index = 0;
                Paper.book().delete(Prevalent.UserEmailKey);
                Paper.book().delete(Prevalent.UserPasswordkey);
                startActivity(intent);
                break;
        }

        if (selectedFragment!= null){
            getSupportFragmentManager().beginTransaction().replace(R.id.myframe, selectedFragment).addToBackStack(null).commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}