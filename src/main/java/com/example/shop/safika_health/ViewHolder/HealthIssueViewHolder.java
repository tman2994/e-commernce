package com.example.shop.safika_health.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.Interface.itemClickListener;
import com.example.shop.safika_health.R;

public class HealthIssueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtIssue;
    public itemClickListener listener;

    public HealthIssueViewHolder(@NonNull View itemView) {
        super(itemView);

        txtIssue = (TextView) itemView.findViewById(R.id.item_health_issue);
    }

    public void setItemClickListener(itemClickListener listener) {

            this.listener = listener; // is this a error

    }
    @Override
    public void onClick(View v) {

        listener.onClick(v, getAdapterPosition(), false);

    }
}
