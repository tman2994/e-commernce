package com.example.shop.safika_health.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.Activities.DashboardActivity;
import com.example.shop.safika_health.Activities.HomeActivity;
import com.example.shop.safika_health.Adapter.CartAdapter;
import com.example.shop.safika_health.Adapter.ProductAdapter;
import com.example.shop.safika_health.BuildConfig;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductsFragment extends Fragment {
    private RecyclerView mRecycleView;
    RecyclerView.LayoutManager layoutMAnager;
    List<Products> product_array = new ArrayList<>();
    RecyclerView.Adapter adapter;
    public static TextView cart_total_amount;
    public static double total_amount = 0;

    public static ProductsFragment getInstance(){
        return new ProductsFragment();
    }
    @Nullable
    @Override
    //This fragment is used for Products, Recommended, Favorites, and Carts all.
    // For this, an index value(named product_page_index) is used
    // 0 value of product _page_index menas current page is just Products, 1 means Recommended, 2 means Favorites, 3 means Carts page
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_products, container, false);

        List<String> category_array = new ArrayList<>();
        final List<List<Products>> filter_array = new ArrayList<>();
        List<Products> new_products = new ArrayList<>();


        category_array.add("Category:");
        category_array.add("All");

        LinearLayout cart_view = (LinearLayout) view.findViewById(R.id.cart_frame);
        cart_view.setVisibility(LinearLayout.GONE);

        ImageView logo = view.findViewById(R.id.id_dashboard_logo);
        logo.setVisibility(View.INVISIBLE);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout3);
        ConstraintLayout.LayoutParams parameter = (ConstraintLayout.LayoutParams) linearLayout.getLayoutParams();
        parameter.setMargins(parameter.leftMargin, 30, parameter.rightMargin, parameter.bottomMargin); // left, top, right, bottom
        linearLayout.setLayoutParams(parameter);

        //This is for Products page
        if (Prevalent.product_page_index == 0) {
            new_products = new ArrayList<>(Prevalent.all_products);
            product_array = new ArrayList<>(Prevalent.all_products);
            filter_array.add(Prevalent.all_products);
            logo.setVisibility(View.VISIBLE);
            parameter.setMargins(parameter.leftMargin, 170, parameter.rightMargin, parameter.bottomMargin); // left, top, right, bottom
            linearLayout.setLayoutParams(parameter);

        }
        // This is for Recommended Page
        else if (Prevalent.product_page_index == 1) {
            product_array = new ArrayList<>(Prevalent.recommended_products);
            new_products = new ArrayList<>(Prevalent.recommended_products);
            filter_array.add(Prevalent.recommended_products);
        }
        // This is for Favorites page
        else if (Prevalent.product_page_index == 2){
            product_array = new ArrayList<>(Prevalent.favorited_products);
            new_products = new ArrayList<>(Prevalent.favorited_products);
            filter_array.add(Prevalent.favorited_products);
        }
        // This is for Carts page
        else {
            product_array = new ArrayList<>(Prevalent.cart_products);
            new_products = new ArrayList<>(Prevalent.cart_products);
            filter_array.add(Prevalent.cart_products);
            cart_view.setVisibility(LinearLayout.VISIBLE);
            total_amount = 0;
            cart_total_amount = view.findViewById(R.id.id_cart_total);
            for (Products product: Prevalent.cart_products) {
                total_amount += Double.valueOf(product.getPrice());
            }
            cart_total_amount.setText("£" + String.valueOf(total_amount));
            view.findViewById(R.id.id_cart_checkout).setOnClickListener(new View.OnClickListener() {
               //sends submit to check out would conect to paypal functionality
                @Override
                public void onClick(View v) {
                    product_array.clear();
                    adapter.notifyDataSetChanged();
                    Prevalent.cart_products.clear();
                    cart_total_amount.setText("£0");
                    Toast.makeText(getContext(), "Success to checkout", Toast.LENGTH_SHORT).show();

                }

            });
        }

        //Create category list from products
        for(Products product: new_products) {
            if (!category_array.contains(product.getCategory())) {
                category_array.add(product.getCategory());
                List<Products> array = new ArrayList<>();
                array.add(product);
                filter_array.add(array);
            }
            else {
                int index = category_array.indexOf(product.getCategory());
                filter_array.get(index - 1).add(product);
            }
        }

        //Create drop list for category filter
        Spinner category_spinner = (Spinner)view.findViewById(R.id.id_category_spinner);
        ArrayAdapter<String> categoryArrayAdapter = new ArrayAdapter<String>(getContext(),   android.R.layout.simple_spinner_item, category_array);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        category_spinner.setAdapter(categoryArrayAdapter);

        //Create drop list for sort filter
        Spinner sort_spinner = (Spinner)view.findViewById(R.id.id_sort_spinner);
        final String sort_array[] = {"Sort By:", "Price Low - High","Price High - Low","Most Favorited","Least Favorited"};
        ArrayAdapter<String> sortArrayAdapter = new ArrayAdapter<String>(getContext(),   android.R.layout.simple_spinner_item, sort_array);
        sortArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sort_spinner.setAdapter(sortArrayAdapter);



        mRecycleView = view.findViewById(R.id.id_product_recycle);
        mRecycleView.setHasFixedSize(true);
        layoutMAnager = new LinearLayoutManager(requireContext());
        mRecycleView.setLayoutManager(layoutMAnager);

        mRecycleView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        //Assign adapter either CartAdapter or ProductAdapter according current page is for Carts or not
        if (Prevalent.product_page_index == 3) {
            adapter = new CartAdapter(product_array);
        }
        else {
            adapter = new ProductAdapter(product_array);
        }

        mRecycleView.setAdapter(adapter);

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position != 0) {
                    product_array.clear();
                    product_array.addAll(filter_array.get(position - 1));
                    adapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        //Select action of sort drop list item
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
                    case 1:
                        Collections.sort(product_array, new Comparator<Products>() {
                            @Override
                            public int compare(Products o1, Products o2) {
                                return  Double.valueOf(o1.getPrice()).compareTo(Double.valueOf(o2.getPrice()));
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(product_array, new Comparator<Products>() {
                            @Override
                            public int compare(Products o1, Products o2) {
                                return  Double.valueOf(o2.getPrice()).compareTo(Double.valueOf(o1.getPrice()));
                            }
                        });
                        break;
                    case 3:
                        Collections.sort(product_array, new Comparator<Products>() {
                            @Override
                            public int compare(Products o1, Products o2) {
                                return  Integer.valueOf(o2.getFavorited()).compareTo(Integer.valueOf(o1.getFavorited()));
                            }
                        });
                        break;
                    case 4:
                        Collections.sort(product_array, new Comparator<Products>() {
                            @Override
                            public int compare(Products o1, Products o2) {
                                return  Integer.valueOf(o1.getFavorited()).compareTo(Integer.valueOf(o2.getFavorited()));
                            }
                        });
                        break;
                    default:
                        break;
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });


        return view;
    }

}

