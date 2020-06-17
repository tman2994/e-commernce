package com.example.shop.safika_health.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shop.safika_health.Activities.HomeActivity;
import com.example.shop.safika_health.Activities.MainActivity;
import com.example.shop.safika_health.Activities.RegisterActivity;
import com.example.shop.safika_health.BuildConfig;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Model.Users;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProductDetailFragment extends Fragment {
    private TextView name, price, allergies, category, description, healthProblem, ingredient, stock, rate_num, favo_num;
    private RatingBar rate_bar, new_rate_bar;

    private ProgressDialog loadingBar;

    public static ProductDetailFragment getInstance(){
        return new ProductDetailFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_product_detail, container, false);


        List<String> array = Prevalent.currentOnlineUser.getFavorited();

        final ImageView favo_image = view.findViewById(R.id.id_favo_icon);
        if (array.contains(Prevalent.detailProduct.getId())) {
            favo_image.setColorFilter(Color.RED);
        } else {
            favo_image.setColorFilter(Color.GRAY);
        }


        favo_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> array = Prevalent.currentOnlineUser.getFavorited();
                int product_favo_num = Integer.valueOf(Prevalent.detailProduct.getFavorited());
                String id = Prevalent.detailProduct.getId();
                if (array.contains(id)) {
                    array.remove(id);
                    product_favo_num --;
                    Prevalent.favorited_products.remove(id);
                    favo_image.setColorFilter(Color.GRAY);
                    Prevalent.favorited_products.remove(Prevalent.detailProduct);
                    Toast.makeText(getContext(), "You removed this product on favorites", Toast.LENGTH_SHORT).show();
                } else {
                    array.add(id);
                    product_favo_num ++;
                    Prevalent.favorited_products.add(Prevalent.detailProduct);
                    favo_image.setColorFilter(Color.RED);

                    Toast.makeText(getContext(), "Success to add this product on favorites", Toast.LENGTH_SHORT).show();
                }
                favo_num.setText("("+ String.valueOf(product_favo_num) + ")");

                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference().child("Users");

                RootRef.orderByChild("id").equalTo(Prevalent.currentOnlineUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists())) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                snapshot.getRef().child("favorited").setValue(array);
                                Prevalent.currentOnlineUser.setFavrited(array);
                            }

                        } else {
                            Toast.makeText(getContext(),"This product is not exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final DatabaseReference RootRef1;
                RootRef1 = FirebaseDatabase.getInstance().getReference().child("product");

                final int favo_num = product_favo_num;
                RootRef1.orderByChild("id").equalTo(Prevalent.detailProduct.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    // Action to update favorite field of product on firebase database when click favorite button
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists())) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                snapshot.getRef().child("favorited").setValue(String.valueOf(favo_num));
                                int index = Prevalent.all_products.indexOf(Prevalent.detailProduct);
                                Prevalent.all_products.get(index).setFavorited(String.valueOf(favo_num));
                                if (Prevalent.favorited_products.contains(Prevalent.detailProduct)) {
                                    index = Prevalent.favorited_products.indexOf(Prevalent.detailProduct);
                                    Prevalent.favorited_products.get(index).setFavorited(String.valueOf(favo_num));
                                }
                                if (Prevalent.recommended_products.contains(Prevalent.detailProduct)) {
                                    index = Prevalent.recommended_products.indexOf(Prevalent.detailProduct);
                                    Prevalent.recommended_products.get(index).setFavorited(String.valueOf(favo_num));
                                }
                            }

                        } else {
                            Toast.makeText(getContext(),"This product is not exist", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        name = view.findViewById(R.id.id_detail_name);
        price = view.findViewById(R.id.id_detail_price);
        allergies = view.findViewById(R.id.id_detail_allergies);
        category = view.findViewById(R.id.id_detail_category);
        description = view.findViewById(R.id.id_detail_description);
        healthProblem = view.findViewById(R.id.id_detail_healthProblem);
        ingredient = view.findViewById(R.id.id_detail_ingredient);
        stock = view.findViewById(R.id.id_detail_stock);
        rate_bar = view.findViewById(R.id.id_detail_ratingBar);
        new_rate_bar = view.findViewById(R.id.id_detail_new_ratingBar);
        rate_num = view.findViewById(R.id.id_detail_rating_num);
        favo_num = view.findViewById(R.id.id_detail_favo_num);

        name.setText(Prevalent.detailProduct.getName());
        price.setText("Price :  " + "£" + Prevalent.detailProduct.getPrice());
        allergies.setText(Prevalent.detailProduct.getAllergies());
        category.setText(Prevalent.detailProduct.getCategory());
        description.setText(Prevalent.detailProduct.getDescription());
        healthProblem.setText(Prevalent.detailProduct.getHealthProblem());
        ingredient.setText(Prevalent.detailProduct.getIngredients());
        stock.setText(Prevalent.detailProduct.getStock());
        rate_bar.setRating(Float.parseFloat(Prevalent.detailProduct.getRate()));
        rate_num.setText("(" + Prevalent.detailProduct.getRate_num() +")");
        favo_num.setText("(" + Prevalent.detailProduct.getFavorited() +")");

        final ImageView image = view.findViewById(R.id.id_detail_image);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.get().load(Prevalent.detailProduct.getImageUrl())
                .error(R.drawable.books)
                .placeholder(R.drawable.cart)
                .into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPhoto(image, 250, 250);
            }
        });
        (view.findViewById(R.id.ly_ratingbar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRate(rate_bar, 250, 250);
            }
        });

        Button add_cart_btn = (Button) view.findViewById(R.id.id_detail_add_cart);
        add_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Prevalent.cart_products.add(Prevalent.detailProduct);
               Toast.makeText(getContext(), "Success to add this product in your basktet", Toast.LENGTH_SHORT).show();

            }

        });

        return view;

    }

    public void loadPhoto(ImageView imageView, int width, int height) {

        ImageView tempImageView = imageView;


        AlertDialog.Builder imageDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                (ViewGroup) getView().findViewById(R.id.layout_root));
        ImageView image = layout.findViewById(R.id.fullimage);
        image.setImageDrawable(tempImageView.getDrawable());
        imageDialog.setView(layout);
        imageDialog.setPositiveButton("Close", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });


        imageDialog.create();
        imageDialog.show();
    }

    public void addRate(RatingBar rateBar, int width, int height) {

        final AlertDialog.Builder rateDialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.add_rate_dialog,
                (ViewGroup) getView().findViewById(R.id.layout_rate_root));
        final RatingBar rate = layout.findViewById(R.id.id_detail_new_ratingBar);

        rateDialog.setView(layout);


        rateDialog.setPositiveButton("Done", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {

                final String new_rate = String.valueOf((float) rate.getRating());

                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference().child("product");


                final String product_id = Prevalent.detailProduct.getId();

                RootRef.orderByChild("id").equalTo(product_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists())) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                int new_rate_num = 0;
                                float new_rate1 = 0;
                                if (snapshot.hasChild("rate_num")) {
                                    int old_rate_num = Integer.parseInt(snapshot.child("rate_num").getValue(String.class));
                                    float old_rate = Float.parseFloat(snapshot.child("rate").getValue(String.class));
                                    new_rate_num = old_rate_num + 1;
                                    new_rate1 = ((old_rate * old_rate_num) + Float.parseFloat(new_rate)) / new_rate_num;

                                }
                                else {
                                    new_rate_num = 1;
                                    new_rate1 = Float.parseFloat(new_rate);
                                }

                                snapshot.getRef().child("rate_num").setValue(String.valueOf(new_rate_num));
                                snapshot.getRef().child("rate").setValue(String.valueOf(new_rate1));
                                rate_bar.setRating(new_rate1);
                                rate_num.setText("(" + new_rate_num +")");

                                int index = Prevalent.all_products.indexOf(Prevalent.detailProduct);
                                Prevalent.all_products.get(index).setRate(String.valueOf(new_rate1));
                                Prevalent.all_products.get(index).setRate_num(String.valueOf(new_rate_num));
                                if (Prevalent.favorited_products.contains(Prevalent.detailProduct)) {
                                    index = Prevalent.favorited_products.indexOf(Prevalent.detailProduct);
                                    Prevalent.favorited_products.get(index).setRate(String.valueOf(new_rate1));
                                    Prevalent.favorited_products.get(index).setRate_num(String.valueOf(new_rate_num));
                                }
                                if (Prevalent.recommended_products.contains(Prevalent.detailProduct)) {
                                    index = Prevalent.recommended_products.indexOf(Prevalent.detailProduct);
                                    Prevalent.recommended_products.get(index).setRate(String.valueOf(new_rate1));
                                    Prevalent.recommended_products.get(index).setRate_num(String.valueOf(new_rate_num));
                                }

                            }

                        } else {
                            Toast.makeText(getContext(),"This product is not exist", Toast.LENGTH_SHORT).show();
//                            loadingBar.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                dialog.dismiss();
            }



        });

        final AlertDialog alert = rateDialog.create();
        alert.show();
    }
}
