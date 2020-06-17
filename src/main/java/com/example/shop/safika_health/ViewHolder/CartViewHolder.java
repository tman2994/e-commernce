package com.example.shop.safika_health.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.Interface.itemClickListener;
import com.example.shop.safika_health.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductNumber, txtProductPrice;
    public ImageView imageView;
    public Button removeBtn;
    public itemClickListener listener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.item_cart_image);
        txtProductName = (TextView) itemView.findViewById(R.id.item_cart_name);
        txtProductPrice = (TextView) itemView.findViewById(R.id.item_cart_price);
        txtProductNumber = (TextView) itemView.findViewById(R.id.item_cart_number);
        removeBtn = (Button) itemView.findViewById(R.id.id_cart_remove);
    }

    public void setItemClickListener(itemClickListener listener) {

            this.listener = listener; // is this a error

    }
    @Override
    public void onClick(View v) {

        listener.onClick(v, getAdapterPosition(), false);

    }
}
