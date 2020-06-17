package com.example.shop.safika_health.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.Activities.DashboardActivity;
import com.example.shop.safika_health.Activities.HomeActivity;
import com.example.shop.safika_health.Fragments.ProductDetailFragment;
import com.example.shop.safika_health.Fragments.ProductsFragment;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.example.shop.safika_health.ViewHolder.CartViewHolder;
import com.example.shop.safika_health.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Products> products;

    public CartAdapter(List<Products> products) {
        this.products = products;
    }

    public Context getContext() {
        return context;
    }

    private Context context;



    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        final Products model = products.get(position);
        holder.txtProductName.setText(model.getName());
        holder.txtProductPrice.setText("Price = " + "£" + model.getPrice());

        Picasso.get().load(model.getImageUrl())
                .error(R.drawable.books)
                .placeholder(R.drawable.cart)
                .into(holder.imageView);

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prevalent.detailProduct = model;
                Fragment newFragment = new ProductDetailFragment();
                FragmentTransaction transaction = ((DashboardActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        holder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Do you really want remove this?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                products.remove(model);
                                double total_amount = 0;
                                for(Products product: products) {
                                    total_amount += Double.valueOf(product.getPrice());
                                }
                                ProductsFragment.cart_total_amount.setText("£" + String.valueOf(total_amount));
                                ProductsFragment.total_amount = total_amount;
                                notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return products.size();
    }

}
