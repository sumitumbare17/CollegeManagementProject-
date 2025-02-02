package com.example.collegemanagementproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class QueryAdapterFaculty extends RecyclerView.Adapter<QueryAdapterFaculty.ViewHolder> {
    private List<QueryModel> queryList;
    private OnResponseListener responseListener;
    private boolean isFacultyView;

    public interface OnResponseListener {
        void onResponseSubmit(String queryId, String response);
    }

    public QueryAdapterFaculty(List<QueryModel> queryList, OnResponseListener responseListener, boolean isFacultyView) {
        this.queryList = queryList;
        this.responseListener = responseListener;
        this.isFacultyView = isFacultyView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_query_faculty, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QueryModel query = queryList.get(position);

        // Debug log
        Log.d("QueryAdapterFaculty", "Query ID at position " + position + ": " + query.getQueryId());

        holder.tvQueryText.setText(query.getQueryText());

        if (query.getResponse() != null && !query.getResponse().isEmpty()) {
            holder.tvResponse.setText("Response: " + query.getResponse());
        } else {
            holder.tvResponse.setText("No response yet.");
        }

        if (isFacultyView) {
            holder.etResponse.setVisibility(View.VISIBLE);
            holder.btnSubmitResponse.setVisibility(View.VISIBLE);

            holder.btnSubmitResponse.setOnClickListener(v -> {
                String responseText = holder.etResponse.getText().toString().trim();
                if (!responseText.isEmpty() && query.getQueryId() != null) {
                    responseListener.onResponseSubmit(query.getQueryId(), responseText);
                } else {
                    Log.e("QueryAdapterFaculty", "Query ID is missing!");
                }
            });
        } else {
            holder.etResponse.setVisibility(View.GONE);
            holder.btnSubmitResponse.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return queryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQueryText, tvResponse;
        EditText etResponse;
        Button btnSubmitResponse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQueryText = itemView.findViewById(R.id.tvQueryText);
            tvResponse = itemView.findViewById(R.id.tvResponse);
            etResponse = itemView.findViewById(R.id.etResponse);
            btnSubmitResponse = itemView.findViewById(R.id.btnSubmitResponse);
        }
    }
}
