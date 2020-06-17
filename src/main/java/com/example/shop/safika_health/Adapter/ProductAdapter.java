package com.example.shop.safika_health.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.Activities.DashboardActivity;
import com.example.shop.safika_health.Activities.HomeActivity;
import com.example.shop.safika_health.Fragments.ProductDetailFragment;
import com.example.shop.safika_health.Model.Products;
import com.example.shop.safika_health.Prevalent.Prevalent;
import com.example.shop.safika_health.R;
import com.example.shop.safika_health.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private List<Products> products;

    public Context getContext() {
        return context;
    }

    private Context context;

    public ProductAdapter(List<Products> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        final Products model = products.get(position);
        holder.txtProductName.setText(model.getName());
        if (Prevalent.product_page_index != 1) {
            holder.getTxtProductDescription.setText(model.getAllergies());
        }
        else {
            List<String> my_issues = Prevalent.currentOnlineUser.getHealthProblems();
            List<String> array = Arrays.asList(model.getHealthProblem().split(","));
            String issue = "";
            for (int i = 0 ;i<array.size();i++) {
                if (my_issues.contains(array.get(i))) {
                    if (issue == "") {
                        issue += array.get(i);
                    }
                    else {
                        issue +=  ", " + array.get(i);
                    }

                }
            }
            holder.getTxtProductDescription.setText(model.getAllergies() + " (This was recommened for " + issue + ")");
        }

        holder.txtProductPrice.setText("Price = " + "£" + model.getPrice());
        holder.txtFavoNum.setText("(" + model.getFavorited() + ")");

        String imageUrl = model.getImageUrl().length() == 0 ? "https://f0.pngfuel.com/png/809/958/black-shopping-cart-clip-art-png-clip-art.png":model.getImageUrl();
        Picasso.get().load(imageUrl)
                .error(R.drawable.books)
                .placeholder(R.drawable.cart)
                .into(holder.imageView);
        if (model.getFavorited() != "0") {
            holder.imgFavoIcon.setColorFilter(Color.RED);
        } else {
            holder.imgFavoIcon.setColorFilter(Color.GRAY);
        }

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
    }



    @Override
    public int getItemCount() {
        return products.size();
    }

}
