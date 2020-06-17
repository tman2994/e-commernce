package com.example.shop.safika_health.ViewHolder;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.Interface.itemClickListener;
import com.example.shop.safika_health.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, getTxtProductDescription, txtProductPrice, txtFavoNum;
    public ImageView imageView, imgFavoIcon;
    public itemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.item_product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.item_product_name);
        getTxtProductDescription = (TextView) itemView.findViewById(R.id.item_product_desc);
        txtProductPrice = (TextView) itemView.findViewById(R.id.item_product_price);
        txtFavoNum = (TextView) itemView.findViewById(R.id.item_product_favo_num);
        imgFavoIcon = (ImageView) itemView.findViewById(R.id.item_product_favo_icon);
    }

    public void setItemClickListener(itemClickListener listener) {

            this.listener = listener; // is this a error

    }
    @Override
    public void onClick(View v) {

        listener.onClick(v, getAdapterPosition(), false);

    }
}
