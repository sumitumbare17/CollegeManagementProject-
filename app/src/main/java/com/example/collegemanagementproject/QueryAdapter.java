package com.example.collegemanagementproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.QueryViewHolder> {
    private List<QueryModel> queryList;

    public QueryAdapter(StudentAddQuery studentAddQuery, List<QueryModel> queryList) { this.queryList = queryList; }

    @NonNull
    @Override
    public QueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_query, parent, false);
        return new QueryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueryViewHolder holder, int position) {
        QueryModel query = queryList.get(position);
        holder.tvQueryText.setText("Query: " + query.getQueryText());
        holder.tvResponse.setText(query.getResponse().isEmpty() ? "No Response Yet" : "Response: " + query.getResponse());
    }

    @Override
    public int getItemCount() { return queryList.size(); }

    static class QueryViewHolder extends RecyclerView.ViewHolder {
        TextView tvQueryText, tvResponse;
        QueryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQueryText = itemView.findViewById(R.id.tvQueryText);
            tvResponse = itemView.findViewById(R.id.tvResponse);
        }
    }
}
