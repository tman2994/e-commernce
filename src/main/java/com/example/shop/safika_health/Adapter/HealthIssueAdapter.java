package com.example.shop.safika_health.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shop.safika_health.R;
import com.example.shop.safika_health.ViewHolder.HealthIssueViewHolder;
import java.util.List;

public class HealthIssueAdapter extends RecyclerView.Adapter<HealthIssueViewHolder> {

    private List<String> issues;

    public Context getContext() {
        return context;
    }

    private Context context;

    public HealthIssueAdapter(List<String> products) {
        this.issues = products;
    }

    @NonNull
    @Override
    public HealthIssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.health_issue_item, parent, false);
        return new HealthIssueViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull HealthIssueViewHolder holder, int position) {

        final String issue = issues.get(position);
        holder.txtIssue.setText(issue);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public void setModels(List<String> my_issues) {
        this.issues = my_issues;
        notifyDataSetChanged();
    }
}
