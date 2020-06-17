package com.example.shop.safika_health.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shop.safika_health.Fragments.ProductsFragment;
import com.example.shop.safika_health.Fragments.ProfileFragment;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    lateinit var toolbar: ActionBar
BottomNavigationView bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /// Actvity initialize
        setContentView(R.layout.activity_dashboard);

        /// bottom navigation initialize
        bottomNavigation = findViewById(R.id.navigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        /// initialized first page with product page
        openFragment(ProductsFragment.getInstance());
    }

    //// function to change fragment
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //// add listener to select bottom navigation items
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                            /// product page when click first item
                        case R.id.navigation_product:
                            Prevalent.product_page_index = 0;
                            openFragment(ProductsFragment.getInstance());
                            return true;
                            /// favorite page when click second item
                        case R.id.navigation_recommend:
                            Prevalent.product_page_index = 1;
                            openFragment(ProductsFragment.getInstance());
                            return true;
                            /// recommend page when click third item
                        case R.id.navigation_favorite:
                            Prevalent.product_page_index = 2;
                            openFragment(ProductsFragment.getInstance());
                            return true;
                            /// cart page when click fourth item
                        case R.id.navigation_cart:
                            Prevalent.product_page_index = 3;
                            openFragment(ProductsFragment.getInstance());
                            return true;
                            /// account page when click fifth item
                        case R.id.navigation_account:
                            openFragment(ProfileFragment.getInstance());
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
